package com.urarik.notes_server.objects;

import com.urarik.notes_server.message.Invitation;
import com.urarik.notes_server.note.Block;
import com.urarik.notes_server.note.Note;
import com.urarik.notes_server.note.Text;
import com.urarik.notes_server.project.Project;
import com.urarik.notes_server.security.User;

import java.util.*;

public class TestObjects {
    private static final Map<String, Object> testObjects;

    static {
        testObjects = new HashMap<>();
        testObjects.put("dinesh", new User(
                "dinesh",
                "$2a$10$aYJ7ct3pfGbFzC/mRCxmmedVS7FAkaVcZCmjveDRbfU8lKckjgWVO",
                "USER"
        ));
        testObjects.put("anamika", new User(
                "anamika",
                "$2a$04$HCZQH4c0VIIz0 KxO1Ux.c.REEM.sQZDyA8eZl8A48bBIYIczzSET6",
                "USER"
        ));
        testObjects.put("arnav", new User(
                "arnav",
                "$2a$04$Y5tgmB9IAsE4yPrA. oghQO9jfD6u4qSviHCbVXww3FXgOTnC4da0a",
                "ADMIN"
        ));
        testObjects.put("rushika", new User(
                "rushika",
                "$2a$04$Y5tgmB9IAsE4yPrA. oghQO9jfD6u4qSviHCbVXww3FXgOTnC4da0a",
                "ADMIN"
        ));

        testObjects.put("p1", new Project(
                (User) testObjects.get("dinesh"),
                new HashSet<>(),
                new HashSet<>(),
                "Project1"
        ));
        testObjects.put("p2", new Project(
                (User) testObjects.get("anamika"),
                new HashSet<>(),
                getSet((User) testObjects.get("dinesh"), (User) testObjects.get("rushika")),
                "Project1"
        ));
        testObjects.put("p3", new Project(
                (User) testObjects.get("rushika"),
                getSet((User) testObjects.get("dinesh")),
                new HashSet<>(),
                "Project1"
        ));
        testObjects.put("p4", new Project(
                (User) testObjects.get("dinesh"),
                new HashSet<>(),
                new HashSet<>(),
                "Project1"
        ));

        testObjects.put("n1", new Note(
                (Project) testObjects.get("p2"),
                1L,
                false,
                "a",
                getSet((User) testObjects.get("dinesh"), (User) testObjects.get("anamika"))
        ));
        testObjects.put("n2", new Note(
                (Project) testObjects.get("p2"),
                2L,
                true,
                "b",
                getSet((User) testObjects.get("dinesh"), (User) testObjects.get("anamika"))
        ));
        testObjects.put("n3", new Note(
                (Project) testObjects.get("p2"),
                3L,
                false,
                "c",
                getSet((User) testObjects.get("rushika"), (User) testObjects.get("anamika"))
        ));
        testObjects.put("n4", new Note(
                (Project) testObjects.get("p4"),
                1L,
                false,
                "d",
                getSet((User) testObjects.get("dinesh"))
        ));

        testObjects.put("b1", new Block(
                (Note) testObjects.get("n1"),
                1L,
                "Text"
        ));
        testObjects.put("b2", new Block(
                (Note) testObjects.get("n1"),
                2L,
                "Text"
        ));

        testObjects.put("t1", new Text(
                "text1",
                (Block) testObjects.get("b1")
        ));
        testObjects.put("t2", new Text(
                "text2",
                (Block) testObjects.get("b2")
        ));

        testObjects.put("i1", new Invitation(
                (User) testObjects.get("rushika"),
                (User) testObjects.get("dinesh"),
                (Project) testObjects.get("p1")
        ));
        testObjects.put("i2", new Invitation(
                (User) testObjects.get("arnav"),
                (User) testObjects.get("dinesh"),
                (Project) testObjects.get("p1")
        ));
        testObjects.put("i3", new Invitation(
                (User) testObjects.get("arnav"),
                (User) testObjects.get("rushika"),
                (Project) testObjects.get("p3")
        ));

    }

    public static Set<User> getSet(User ...users) {
        Set<User> userSet = new HashSet<>();
        Collections.addAll(userSet, users);
        return userSet;
    }
    public static Set<User> getSet(List<String> users) {
        Set<User> userSet = new HashSet<>();
        users.forEach(user -> userSet.add((User) TestObjects.get(user)));
        return userSet;
    }

    public static Object get(String key) {
        System.out.println(key + " called");
        return testObjects.get(key);
    }

}
