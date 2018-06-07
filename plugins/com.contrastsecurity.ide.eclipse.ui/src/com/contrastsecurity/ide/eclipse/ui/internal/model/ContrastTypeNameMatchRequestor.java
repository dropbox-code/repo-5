package com.contrastsecurity.ide.eclipse.ui.internal.model;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.search.TypeNameMatch;
import org.eclipse.jdt.core.search.TypeNameMatchRequestor;

public class ContrastTypeNameMatchRequestor extends TypeNameMatchRequestor {

	private Set<IType> typeNameMatches = new HashSet<>();

	@Override
	public void acceptTypeNameMatch(TypeNameMatch match) {
		typeNameMatches.add(match.getType());
	}

	public Set<IType> getTypeNameMatches() {
		return typeNameMatches;
	}

}
