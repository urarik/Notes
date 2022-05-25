package com.urarik.notes_server.analysis.sd;

import java.util.List;
import java.util.Map;

public interface SDRepository {
    List<Map<String, Object>> getInvokes(Long pid, Long mid);
    List<Map<String, Object>> getBefore(Long pid, Long mid);
}
