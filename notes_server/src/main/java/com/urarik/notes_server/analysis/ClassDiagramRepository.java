package com.urarik.notes_server.analysis;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public interface ClassDiagramRepository {
    Map<String, List<JSONObject>> findMethodMapInIds(List<Long> cid, Long pid);
    Map<String, List<JSONObject>> findMemberMapInIds(List<Long> cid, Long pid);
    Map<String, JSONObject> findEntityMapInIds(List<Long> eidList, Long pid);
}
