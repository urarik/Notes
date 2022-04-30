package com.urarik.notes_server.analysis.dto;

public class AnalyzeRequest {
    private String link;
    private Long pid;

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }
}
