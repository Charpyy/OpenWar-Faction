package com.openwar.openwarfaction.factions;

public enum Permission {
    RENAME(0x001),
    HOME(0x002),
    SETHOME(0x004),
    CLAIM(0x008),
    INVITE(0x010),
    KICK(0x020),
    BUILD(0x040),
    CONTAINERS(0x080),
    INTERACT(0x0100),
    DIPLOMACY(0x0200);
    
    private final int flag;
    private Permission(int flag) {
        this.flag = flag;
    }
    public int getFlag(){
        return this.flag;
    }
}
public enum PermRank {
    OFFICER(0),
    MEMBER(1),
    RECRUE(2),
    ALLY(3),
    NEUTRAL(4);
    private byte order;
    private PermRank(byte n){
        this.order=n;
    }
    public byte getOrder(){
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