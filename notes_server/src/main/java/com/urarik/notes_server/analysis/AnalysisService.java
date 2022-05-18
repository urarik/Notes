package com.urarik.notes_server.analysis;

import com.urarik.notes_server.analysis.ClassDiagramRepository;
import com.urarik.notes_server.analysis.ClassDiagramRepositoryImpl;
import com.urarik.notes_server.analysis.PlaneRepository;
import com.urarik.notes_server.analysis.table.Entity;
import com.urarik.notes_server.analysis.table.Plane;
import com.urarik.notes_server.analysis.table.Relationship;
import com.urarik.notes_server.project.ProjectService;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class AnalysisService {
    private final ProjectService projectService;
    private final PlaneRepository planeRepository;
    private final ClassDiagramRepository classDiagramRepository;

    public AnalysisService(ProjectService projectService, PlaneRepository planeRepository, ClassDiagramRepositoryImpl classDiagramRepository) {
        this.projectService = projectService;
        this.planeRepository = planeRepository;
        this.classDiagramRepository = classDiagramRepository;
    }

    public Map<Object, Object> getClassDiagram(Long pid, Long planeId, String userName) throws IllegalAccessException {
        if(!projectService.isMember(pid, userName))
            throw new IllegalAccessException("");

        Map<Object, Object> map = new HashMap<>();
        planeRepository.findById(planeId).ifPresent(plane -> {
            if(!Objects.equals(plane.getPid(), pid)) return;
            Set<Entity> entitySet = plane.getEntitySet();
            Set<Relationship> relationshipSet = plane.getRelationshipSet();
            plane.setEntitySet(null);
            plane.setRelationshipSet(null);
            map.put("plane", plane);

            Map<String, List<JSONObject>> methodMap =
                    classDiagramRepository.findMethodMapInIds(
                            entitySet.stream()
                                    .map(Entity::getId)
                                    .collect(Collectors.toList()),
                            pid
                    );

            Map<String, List<JSONObject>> memberMap =
                    classDiagramRepository.findMemberMapInIds(
                            entitySet.stream()
                                    .map(Entity::getId)
                                    .collect(Collectors.toList()),
                            pid
                    );
            Map<String, JSONObject>entityMap =
                    classDiagramRepository.findEntityMapInIds(
                            entitySet.stream()
                                    .map(Entity::getId)
                                    .collect(Collectors.toList()),
                            pid
                    );
            Map<Long, Map<String, Object>> fullEntityMap = new HashMap<>();
            entitySet.forEach(entity -> fullEntityMap.put(
                    entity.getId(),
                    getFullEntity(entity, methodMap, memberMap, entityMap)));
            map.put("entities", fullEntityMap);
            map.put("relationships", relationshipSet);

        });

        return map;
    }

    Map<String, Object> getFullEntity(Entity entity, Map<String, List<JSONObject>> methodMap, Map<String, List<JSONObject>> memberMap, Map<String, JSONObject> entityMap) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("absLeft", entity.getAbsLeft())
                .put("absTop", entity.getAbsTop())
                .put("absW", entity.getAbsW())
                .put("absH", entity.getAbsH())
                .put("fontSize", entity.getFontSize());

        jsonObject.put("Members", memberMap.get(entity.getId().toString()))
                .put("Methods", methodMap.get(entity.getId().toString()));


        JSONObject entityJson = entityMap.get(entity.getId().toString());
        for(String key : JSONObject.getNames(entityJson))
        {
            jsonObject.put(key, entityJson.get(key));
        }
        return jsonObject.toMap();
    }

    public boolean createPlane(Plane plane) {
        planeRepository.save(plane);
        return true;
    }


}
