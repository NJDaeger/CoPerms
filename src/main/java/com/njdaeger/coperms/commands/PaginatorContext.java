package com.njdaeger.coperms.commands;

import com.njdaeger.coperms.data.CoWorld;
import com.njdaeger.coperms.tree.PermissionTree;
import com.njdaeger.pdk.command.CommandContext;

public record PaginatorContext(CommandContext ctx, PermissionTree tree, CoWorld world, boolean isGroup) {
}
