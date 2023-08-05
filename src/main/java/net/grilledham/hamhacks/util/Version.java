package net.grilledham.hamhacks.util;

import net.grilledham.hamhacks.HamHacksClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Version {
	
	private final int major;
	private final int minor;
	private final int patch;
	private final VersionType type;
	private final int betaVersion;
	
	public Version(String version) {
		this.major = getVersionNumber(version, 0);
		this.minor = getVersionNumber(version, 1);
		this.patch = getVersionNumber(version, 2);
		this.type = getVersionType(version);
		this.betaVersion = getBetaVersion(version);
	}
	
	public String getVersion(int parts, boolean versionType) {
		switch(parts) {
			case -1:
				if(this.minor != 0) {
					if(this.patch != 0) {
						if(this.type != VersionType.RELEASE && versionType) {
							if(this.type == VersionType.BETA) {
								return String.format("%d.%d.%d-%s.%d", this.major, this.minor, this.patch, this.type.toString().toLowerCase(), this.betaVersion);
							} else {
								return String.format("%d.%d.%d-%s", this.major, this.minor, this.patch, this.type.toString().toLowerCase());
							}
						} else {
							return String.format("%d.%d.%d", this.major, this.minor, this.patch);
						}
					} else {
						if(this.type != VersionType.RELEASE && versionType) {
							if(this.type == VersionType.BETA) {
								return String.format("%d.%d-%s.%d", this.major, this.minor, this.type.toString().toLowerCase(), this.betaVersion);
							} else {
								return String.format("%d.%d-%s", this.major, this.minor, this.type.toString().toLowerCase());
							}
						} else {
							return String.format("%d.%d", this.major, this.minor);
						}
					}
				} else {
					if(this.type != VersionType.RELEASE && versionType) {
						if(this.type == VersionType.BETA) {
							return String.format("%d-%s.%d", this.major, this.type.toString().toLowerCase(), this.betaVersion);
						} else {
							return String.format("%d-%s", this.major, this.type.toString().toLowerCase());
						}
					} else {
						return String.format("%d", this.major);
					}
				}
			case 0:
				if(this.patch != 0) {
					if(this.type != VersionType.RELEASE && versionType) {
						if(this.type == VersionType.BETA) {
							return String.format("%d.%d.%d-%s.%d", this.major, this.minor, this.patch, this.type.toString().toLowerCase(), this.betaVersion);
						} else {
							return String.format("%d.%d.%d-%s", this.major, this.minor, this.patch, this.type.toString().toLowerCase());
						}
					} else {
						return String.format("%d.%d.%d", this.major, this.minor, this.patch);
					}
				} else {
					if(this.type != VersionType.RELEASE && versionType) {
						if(this.type == VersionType.BETA) {
							return String.format("%d.%d-%s.%d", this.major, this.minor, this.type.toString().toLowerCase(), this.betaVersion);
						} else {
							return String.format("%d.%d-%s", this.major, this.minor, this.type.toString().toLowerCase());
						}
					} else {
						return String.format("%d.%d", this.major, this.minor);
					}
				}
			case 1:
				if(this.type != VersionType.RELEASE && versionType) {
					if(this.type == VersionType.BETA) {
						return String.format("%d-%s.%d", this.major, this.type.toString().toLowerCase(), this.betaVersion);
					} else {
						return String.format("%d-%s", this.major, this.type.toString().toLowerCase());
					}
				} else {
					return String.format("%d", this.major);
				}
			case 2:
				if(this.type != VersionType.RELEASE && versionType) {
					if(this.type == VersionType.BETA) {
						return String.format("%d.%d-%s.%d", this.major, this.minor, this.type.toString().toLowerCase(), this.betaVersion);
					} else {
						return String.format("%d.%d-%s", this.major, this.minor, this.type.toString().toLowerCase());
					}
				} else {
					return String.format("%d.%d", this.major, this.minor);
				}
			default:
				if(this.type != VersionType.RELEASE && versionType) {
					if(this.type == VersionType.BETA) {
						return String.format("%d.%d.%d-%s.%d", this.major, this.minor, this.patch, this.type.toString().toLowerCase(), this.betaVersion);
					} else {
						return String.format("%d.%d.%d-%s", this.major, this.minor, this.patch, this.type.toString().toLowerCase());
					}
				} else {
					return String.format("%d.%d.%d", this.major, this.minor, this.patch);
				}
		}
	}
	
	public int getMajor() {
		return major;
	}
	
	public int getMinor() {
		return minor;
	}
	
	public int getPatch() {
		return patch;
	}
	
	public VersionType getType() {
		return type;
	}
	
	public int getBetaVersion() {
		return betaVersion;
	}
	
	private int compareMajor(Version otherVersion) {
		return Integer.compare(getMajor(), otherVersion.getMajor());
	}
	
	private int compareMinor(Version otherVersion) {
		return Integer.compare(getMinor(), otherVersion.getMinor());
	}
	
	private int comparePatch(Version otherVersion) {
		return Integer.compare(getPatch(), otherVersion.getPatch());
	}
	
	private int compareType(Version otherVersion) {
		if(getType() == VersionType.BETA && otherVersion.getType() == VersionType.BETA) {
			return Integer.compare(getBetaVersion(), otherVersion.getBetaVersion());
		} else {
			return Integer.compare(otherVersion.getType().getValue(), getType().getValue());
		}
	}
	
	public int compare(String otherVersion) {
		return compare(new Version(otherVersion));
	}
	
	public int compare(Version otherVersion) {
		if(compareMajor(otherVersion) != 0) {
			return compareMajor(otherVersion);
		}
		if(compareMinor(otherVersion) != 0) {
			return compareMinor(otherVersion);
		}
		if(comparePatch(otherVersion) != 0) {
			return comparePatch(otherVersion);
		}
		return compareType(otherVersion);
	}
	
	public boolean isNewerThan(String otherVersion) {
		return isNewerThan(new Version(otherVersion));
	}
	
	public boolean isNewerThan(Version otherVersion) {
		return compare(otherVersion) == 1;
	}
	
	
	public boolean isOlderThan(String otherVersion) {
		return isOlderThan(new Version(otherVersion));
	}
	
	public boolean isOlderThan(Version otherVersion) {
		return compare(otherVersion) == -1;
	}
	
	public boolean isSameVersion(String otherVersion) {
		return isSameVersion(new Version(otherVersion));
	}
	
	public boolean isSameVersion(Version otherVersion) {
		return compare(otherVersion) == 0;
	}
	
	private int getVersionNumber(String version, int index) {
		String[] release = version.split("-");
		String[] parts = release[0].split("\\.");
		List<String> allParts = new ArrayList<>();
		Collections.addAll(allParts, parts);
		try {
			return Integer.parseInt(allParts.get(index));
		} catch(NumberFormatException | IndexOutOfBoundsException e) {
			return 0;
		}
	}
	
	private VersionType getVersionType(String version) {
		if(version.split("-").length > 1) {
			if(version.split("-")[1].toLowerCase().startsWith("r")) {
				return VersionType.RELEASE;
			} else if(version.split("-")[1].toLowerCase().startsWith("b")) {
				return VersionType.BETA;
			} else if(version.split("-")[1].toLowerCase().startsWith("d")) {
				return VersionType.DEV;
			}
		}
		return VersionType.RELEASE;
	}
	
	private int getBetaVersion(String version) {
		if(version.split("-").length > 1) {
			if(version.split("-")[1].toLowerCase().split("\\.").length > 1) {
				try {
					return Integer.parseInt(version.split("-")[1].toLowerCase().split("\\.")[1]);
				} catch(NumberFormatException e) {
					HamHacksClient.LOGGER.error("Parsing version", e);
					return 0;
				}
			}
		}
		return 0;
	}
	
	@Override
	public String toString() {
		return getVersion(0, true);
	}
	
	public enum VersionType {
		RELEASE(0),
		BETA(1),
		DEV(0);

		final int value;
		
		VersionType(int value) {
			this.value = value;
		}
		
		public int getValue() {
			return value;
		}
	}
}
