package com.example.analyzerneo4j.service;

import com.example.analyzerneo4j.entity.Project;
import com.example.analyzerneo4j.repository.custom.ClassDiagramRepository;
import com.example.analyzerneo4j.repository.custom.ClassDiagramRepositoryImpl;
import com.example.analyzerneo4j.repository.custom.ListRepository;
import com.example.analyzerneo4j.repository.custom.ListRepositoryImpl;
import com.example.analyzerneo4j.util.*;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.neo4j.driver.types.Relationship;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Component
public class AnalyzerService {
    private final String BASE_URL = "https://github.com";
    private final Parser parser;
    private final EntityContainer entityContainer;
    private final ClassDiagramRepository classDiagramRepository;
    private final ListRepository listRepository;

    public AnalyzerService(Parser parser, EntityContainer entityContainer, ClassDiagramRepositoryImpl classDiagramRepository, ListRepositoryImpl listRepository) {
        this.parser = parser;
        this.entityContainer = entityContainer;
        this.classDiagramRepository = classDiagramRepository;
        this.listRepository = listRepository;
    }


    public String getEntityForClassDiagram(Long pid, Long cid, Long depth) {
        JSONObject json = new JSONObject();
        List<List<JSONObject>> entityResult = new ArrayList<>();
        List<JSONObject> relationshipResult = new ArrayList<>();
        // parallel true?
        StreamSupport.stream(classDiagramRepository.findByIdAndDepth(cid, pid, depth).spliterator(), false)
                .forEach(node -> {
                    List<JSONObject> list = new ArrayList<>();
                    list.add(nodeToJSON(node));
                    classDiagramRepository.findMethodByName(node.id(), pid).forEach(
                            method -> list.add(nodeToJSON(method)));
                    classDiagramRepository.findMemberByName(node.id(), pid).forEach(
                            member -> list.add(nodeToJSON(member)));
                    entityResult.add(list);
                });

        classDiagramRepository.findClassRelationshipById(cid, pid, depth).forEach(rel ->
            relationshipResult.add(relationshipToJSON(rel))
        );
        return json
                .put("entities", entityResult)
                .put("relationships", relationshipResult)
                .toString();
    }
    public String getPackages(Long pid, Long order) {
        return getNodeList(()-> listRepository.findPackage(pid, order).spliterator(), "packages");
    }
    public String getClassAndInterface(Long pid, Long packageId) {
        return getNodeList(()-> listRepository.findClassAndInterface(pid, packageId).spliterator(), "entities");
    }

    private String getNodeList(Supplier<Spliterator<org.neo4j.driver.types.Node>> supplier, String name) {
        JSONObject json = new JSONObject();
        List<JSONObject> packages = new ArrayList<>();
        StreamSupport.stream(supplier.get(), true)
                .forEach(node -> packages.add(nodeToJSON(node)));
        return json
                .put(name, packages)
                .toString();
    }


    private JSONObject nodeToJSON(org.neo4j.driver.types.Node node) {
        return new JSONObject()
                .put("id", node.id())
                .put("label", node.labels())
                .put("properties", node.asMap());
    }
    private JSONObject relationshipToJSON(Relationship relationship) {
        return new JSONObject()
                .put("start", relationship.startNodeId())
                .put("end", relationship.endNodeId())
                .put("type", relationship.type())
                .put("id", relationship.id())
                .put("properties", relationship.asMap());
    }

    public void process(String url, Long pid) throws IOException, URISyntaxException {
        Project project = verifyDup(url, pid);
        if(project == null) return;

        Mapper mapper = new Mapper();
        process(mapper, url, project, List.of(this::collectName));
        mapper.store(entityContainer);
        process(mapper, url, project, List.of(this::extractRelationship, this::extractMethodRelationship));
        mapper.store(entityContainer);
        entityContainer.projectRepository.save(project).subscribe();
    }

    private Project verifyDup(String url, Long pid) {
        Project project = entityContainer.projectRepository.findById(url).block();
        if(project == null) {
            project = new Project(url, Set.of(pid));
            return project;
        } else {
            project.getPids().add(pid);
            return null;
        }
    }

    private void process(Mapper mapper, String url, Project project,
                         List<TriConsumer<Mapper, Node, String, Project>> actionList) throws IOException, URISyntaxException {
        Document initDocument = parser.getDocument(url);
        Elements initList = initDocument.select(".js-details-container .Box-row a.Link--primary");
        Queue<String> queue = new LinkedList<>();
        initList.forEach(item -> queue.add(BASE_URL + item.attributes().get("href")));

        while(!queue.isEmpty()) {
            String next = queue.poll();
            Document document = parser.getDocument(next);
            Elements list = document.select(".js-details-container .Box-row a.Link--primary");
            Elements code = document.select("#raw-url");

            if (list.isEmpty()) {
                String link = code.get(0).attributes().get("href");
                int index = link.lastIndexOf(".");
                if (link.substring(index + 1).equals("java")) {
                    String urlString = BASE_URL + code.get(0).attributes().get("href");
                    String rawCode = parser.getCode(urlString);

                    CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
                    combinedTypeSolver.add(new ReflectionTypeSolver());

                    JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedTypeSolver);
                    StaticJavaParser.getConfiguration().setSymbolResolver(symbolSolver);
                    CompilationUnit root = StaticJavaParser.parse(rawCode);

                    actionList.forEach(action -> action.accept(mapper, root, urlString, project));
                }
            } else {
                for (Element item : list) {
                    queue.add(BASE_URL + item.attributes().get("href"));
                }
            }
        }
        System.out.println();
    }

    private void collectName(Mapper mapper, Node root, String urlString, Project project) {
        // https://stackoverflow.com/questions/31078754/javaparser-visit-all-node-types-with-one-method
        VoidVisitor<Void> nameCollector = new NameCollector(mapper, urlString, project);
        nameCollector.visit((CompilationUnit) root, null);
    }
    private void extractRelationship(Mapper mapper, Node root, String urlString, Project project) {
        RelationshipExtractor relationshipExtractor = new RelationshipExtractor(mapper, entityContainer);
        relationshipExtractor.analyze((CompilationUnit) root);
    }
    private void extractMethodRelationship(Mapper mapper, Node root, String urlString, Project project) {
        MethodRelationshipExtractor methodRelationshipExtractor = new MethodRelationshipExtractor(mapper);
        methodRelationshipExtractor.analyze((CompilationUnit) root);
    }
    private interface TriConsumer<A, B, C, D> {
        void accept(A a, B b, C c, D d);
    }
}