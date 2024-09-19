package com.openwar.openwarfaction.factions;

public enum Rank {
    RECRUE(0),
    MEMBER(1),
    OFFICER(2),
    LEADER(3);
    private int hierarchy;
    private Rank(int n){
        this.hierarchy=n;
    }
    public boolean isAbove(Rank rank){
        return this.hierarchy>rank.hierarchy;
    }
    public boolean isAbove(Rank rank,int ofN){
        return this.hierarchy>=rank.hierarchy+ofN;
    }
}