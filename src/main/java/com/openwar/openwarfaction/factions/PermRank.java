package com.openwar.openwarfaction.factions;

public enum PermRank {
    OFFICER(0),
    MEMBER(1),
    RECRUE(2),
    ALLY(3),
    NEUTRAL(4);
    private int order;
    private PermRank(int n){
        this.order=n;
    }
    public int getOrder(){
        return this.order;
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
}