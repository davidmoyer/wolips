/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2006 The ObjectStyle Group and individual authors of the
 * software. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * 3. The end-user documentation included with the redistribution, if any, must
 * include the following acknowlegement: "This product includes software
 * developed by the ObjectStyle Group (http://objectstyle.org/)." Alternately,
 * this acknowlegement may appear in the software itself, if and wherever such
 * third-party acknowlegements normally appear.
 * 
 * 4. The names "ObjectStyle Group" and "Cayenne" must not be used to endorse or
 * promote products derived from this software without prior written permission.
 * For written permission, please contact andrus@objectstyle.org.
 * 
 * 5. Products derived from this software may not be called "ObjectStyle" nor
 * may "ObjectStyle" appear in their names without prior written permission of
 * the ObjectStyle Group.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * OBJECTSTYLE GROUP OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 * 
 * This software consists of voluntary contributions made by many individuals on
 * behalf of the ObjectStyle Group. For more information on the ObjectStyle
 * Group, please see <http://objectstyle.org/>.
 *  
 */
package org.objectstyle.wolips.eomodeler.actions;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.objectstyle.wolips.eomodeler.Messages;
import org.objectstyle.wolips.eomodeler.editors.EOModelErrorDialog;
import org.objectstyle.wolips.eomodeler.model.EOArgument;
import org.objectstyle.wolips.eomodeler.model.EOAttribute;
import org.objectstyle.wolips.eomodeler.model.EODatabaseConfig;
import org.objectstyle.wolips.eomodeler.model.EOEntity;
import org.objectstyle.wolips.eomodeler.model.EOFetchSpecification;
import org.objectstyle.wolips.eomodeler.model.EOModelObject;
import org.objectstyle.wolips.eomodeler.model.EORelationship;
import org.objectstyle.wolips.eomodeler.model.EOStoredProcedure;

public class DeleteAction extends Action {
	private ISelection mySelection;

	public void dispose() {
		// DO NOTHING
	}

	public void selectionChanged(IAction _action, ISelection _selection) {
		mySelection = _selection;
	}

	public void run() {
		Shell activeShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		Object[] selectedObjects = null;
		if (mySelection instanceof IStructuredSelection) {
			selectedObjects = ((IStructuredSelection) mySelection).toArray();
		}
		if (selectedObjects != null) {
			Set referenceFailures = new HashSet();
			for (int selectedObjectNum = 0; selectedObjectNum < selectedObjects.length; selectedObjectNum++) {
				Object selectedObject = selectedObjects[selectedObjectNum];
				if (selectedObject instanceof EOModelObject) {
					referenceFailures.addAll(((EOModelObject) selectedObject).getReferenceFailures());
				}
			}
			if (!referenceFailures.isEmpty()) {
				new EOModelErrorDialog(activeShell, referenceFailures).open();
			} else if (MessageDialog.openConfirm(activeShell, Messages.getString("delete.objectsTitle"), Messages.getString("delete.objectsMessage"))) {
				for (int selectedObjectNum = 0; selectedObjectNum < selectedObjects.length; selectedObjectNum++) {
					Object selectedObject = selectedObjects[selectedObjectNum];
					if (selectedObject instanceof EOEntity) {
						EOEntity entity = (EOEntity) selectedObject;
						entity.getModel().removeEntity(entity);
					} else if (selectedObject instanceof EORelationship) {
						EORelationship relationship = (EORelationship) selectedObject;
						relationship.getEntity().removeRelationship(relationship, true);
					} else if (selectedObject instanceof EOAttribute) {
						EOAttribute attribute = (EOAttribute) selectedObject;
						attribute.getEntity().removeAttribute(attribute, true);
					} else if (selectedObject instanceof EOFetchSpecification) {
						EOFetchSpecification fetchSpec = (EOFetchSpecification) selectedObject;
						fetchSpec.getEntity().removeFetchSpecification(fetchSpec);
					} else if (selectedObject instanceof EOStoredProcedure) {
						EOStoredProcedure storedProcedure = (EOStoredProcedure) selectedObject;
						storedProcedure.getModel().removeStoredProcedure(storedProcedure);
					} else if (selectedObject instanceof EOArgument) {
						EOArgument argument = (EOArgument) selectedObject;
						argument.getStoredProcedure().removeArgument(argument);
					} else if (selectedObject instanceof EODatabaseConfig) {
						EODatabaseConfig databaseConfig = (EODatabaseConfig) selectedObject;
						databaseConfig.getModel().removeDatabaseConfig(databaseConfig);
					}
				}
			}
		}
	}

	public void runWithEvent(Event _event) {
		run();
	}

	public void run(IAction _action) {
		run();
	}
}