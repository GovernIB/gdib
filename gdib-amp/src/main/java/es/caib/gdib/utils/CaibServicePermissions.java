package es.caib.gdib.utils;

import java.util.Arrays;
import java.util.List;

import org.alfresco.service.cmr.security.PermissionService;

public enum CaibServicePermissions {
	READ(Arrays.asList(PermissionService.READ)),
	WRITE(Arrays.asList(PermissionService.READ,
						PermissionService.WRITE,
						PermissionService.DELETE,
						PermissionService.ADD_CHILDREN,
						PermissionService.READ_PERMISSIONS,
						PermissionService.CHANGE_PERMISSIONS,
						PermissionService.LOCK,
						PermissionService.UNLOCK));

//  permissionDefinitions.xml
//	public static final String READ = "Read";
//
//	public static final String WRITE = "Write";
//	public static final String DELETE = "Delete";
//	public static final String ADD_CHILDREN = "AddChildren";
//	public static final String READ_PERMISSIONS = "ReadPermissions";
//	public static final String CHANGE_PERMISSIONS = "ChangePermissions";
//	public static final String LOCK = "Lock";
//  public static final String UNLOCK = "Unlock";

	private List<String> permissions;

	private CaibServicePermissions(List<String> permissions) {
		this.permissions = permissions;
	}

	public List<String> getPermissions() {
		return permissions;
	}


}
