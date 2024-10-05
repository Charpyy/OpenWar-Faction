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
    DIPLOMACY(0x0200),
    FACCHEST(0x0400),
    FACSHOP(0x0800),
    RANKUP(0x1000),
    DOORS(0x2000);

    private final int flag;
    private Permission(int flag) {
        this.flag = flag;
    }
    public int getFlag(){
        return this.flag;
    }
    public static Permission fromString(String text) {
        for (Permission p : Permission.values()) {
            if (text.toUpperCase().equals(p.name())) {
                return p;
            }
        }
        return null;
    }
}
