package com.openwar.openwarfaction.factions;

public enum PermRank {
    OFFICER(0,"OFF"),
    MEMBER(1,"MEM"),
    RECRUE(2,"REC"),
    ALLY(3,"ALY"),
    NEUTRAL(4,"NEU");
    private int order;
    private String abr;
    private PermRank(int n,String abrev){
        this.order=n;
        this.abr=abrev;
    }
    public int getOrder(){
        return this.order;
    }
    public String getAbr(){
        return this.abr;
    }
    public static PermRank getPermRank(Rank rank){
        if(rank==Rank.RECRUE){
            return PermRank.RECRUE;
        }
        if(rank==Rank.MEMBER){
            return PermRank.MEMBER;
        }
        if(rank==Rank.OFFICER){
            return PermRank.OFFICER;
        }
        return null;
    }
    public static PermRank fromString(String text) {
        for (PermRank p : PermRank.values()) {
            if (text.toUpperCase().equals(p.name())) {
                return p;
            }
        }
        return null;
    }
}