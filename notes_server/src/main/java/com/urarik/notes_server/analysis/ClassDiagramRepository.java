package com.urarik.notes_server.analysis;

import java.util.List;

public interface ClassDiagramInterface {
    List<List<Object>> findMethodListInIds(List<Long> cid);
    List<List<Object>> findMemberListInIds(List<Long> cid);
}
