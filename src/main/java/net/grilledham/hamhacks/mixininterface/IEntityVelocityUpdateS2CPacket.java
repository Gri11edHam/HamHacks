package net.grilledham.hamhacks.mixininterface;

public interface IEntityVelocityUpdateS2CPacket {
	
	void setX(int vx);
	void setY(int vy);
	void setZ(int vz);
	
	default void set(int vx, int vy, int vz) {
		setX(vx);
		setY(vy);
		setZ(vz);
	}
}
