package net.grilledham.hamhacks.mixininterface;

public interface IEntityVelocityUpdateS2CPacket {
	
	void hamHacks$setX(int vx);
	void hamHacks$setY(int vy);
	void hamHacks$setZ(int vz);
	
	default void set(int vx, int vy, int vz) {
		hamHacks$setX(vx);
		hamHacks$setY(vy);
		hamHacks$setZ(vz);
	}
	
	int hamHacks$getX();
	int hamHacks$getY();
	int hamHacks$getZ();
}
