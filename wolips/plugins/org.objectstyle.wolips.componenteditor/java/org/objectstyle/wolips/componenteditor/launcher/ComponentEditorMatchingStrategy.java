/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2005 The ObjectStyle Group and individual authors of the
 * software. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 1.
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. The end-user documentation
 * included with the redistribution, if any, must include the following
 * acknowlegement: "This product includes software developed by the ObjectStyle
 * Group (http://objectstyle.org/)." Alternately, this acknowlegement may
 * appear in the software itself, if and wherever such third-party
 * acknowlegements normally appear. 4. The names "ObjectStyle Group" and
 * "Cayenne" must not be used to endorse or promote products derived from this
 * software without prior written permission. For written permission, please
 * contact andrus@objectstyle.org. 5. Products derived from this software may
 * not be called "ObjectStyle" nor may "ObjectStyle" appear in their names
 * without prior written permission of the ObjectStyle Group.
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
 * This software consists of voluntary contributions made by many individuals
 * on behalf of the ObjectStyle Group. For more information on the ObjectStyle
 * Group, please see <http://objectstyle.org/> .
 *  
 */
package org.objectstyle.wolips.componenteditor.launcher;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorMatchingStrategy;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.ide.ResourceUtil;
import org.eclipse.ui.part.FileEditorInput;
import org.objectstyle.wolips.componenteditor.ComponenteditorPlugin;
import org.objectstyle.wolips.componenteditor.part.ComponentEditor;
import org.objectstyle.wolips.components.input.ComponentEditorInput;

public class ComponentEditorMatchingStrategy implements IEditorMatchingStrategy {
	
	
	public boolean matches(IEditorReference editorReference,
			IEditorInput editorInput) {
		String editorID = editorReference.getId();
		if(editorID == null) {
			return false;
		}
		if(!editorID.equals(ComponenteditorPlugin.ComponentEditorID)) {
			return false;
		}
		if (editorInput instanceof ComponentEditorInput) {
			IWorkbenchPart workbenchPart = editorReference.getPart(true);
			if(workbenchPart == null) {
				return false;
			}
			ComponentEditor componentEditor = (ComponentEditor)workbenchPart;
			ComponentEditorInput componentEditorInput = componentEditor.getComponentEditorInput();
			return componentEditorInput.equals(editorInput);
		}
		if (!(editorInput instanceof FileEditorInput)) {
			return false;
		}
		IFile inputFile = ResourceUtil.getFile(editorInput);
		if (inputFile == null) {
			return false;
		}
		String extension = inputFile.getFileExtension();
		if (extension == null) {
			return false;
		}
		if (!ComponenteditorPlugin.getDefault().canHandleExtension(extension)) {
			return false;
		}
//		IEditorInput editorReferenceEditorInput = null;
		//expensive: call it as late as possible
		IWorkbenchPart workbenchPart = editorReference.getPart(true);
		if(workbenchPart == null) {
			return false;
		}
		ComponentEditor componentEditor = (ComponentEditor)workbenchPart;
		ComponentEditorInput componentEditorInput = componentEditor.getComponentEditorInput();
//		if(editorReferenceEditorInput == null) {
//			return false;
//		}
//		if(!(editorReferenceEditorInput instanceof ComponentEditorInput)) {
//			return false;
//		}
//		ComponentEditorInput componentEditorInput = (ComponentEditorInput)editorReferenceEditorInput;
		IEditorInput[] editorInputArray = componentEditorInput.getInput();
		for (int i = 0; i < editorInputArray.length; i++) {
			IFile inputFileFromEditor = ResourceUtil.getFile(editorInputArray[i]);
			if(inputFileFromEditor == null) {
				continue;
			}
			if(inputFileFromEditor.equals(inputFile)) {
				switch (i) {
				case 0:
					componentEditor.switchToJava();
					break;
				case 1:
					componentEditor.switchToHtml();
					break;
				case 2:
					componentEditor.switchToWod();
					break;
				case 3:
					componentEditor.switchToApi();
					break;

				default:
					break;
				}
				return true;
			}
			
		}
		return false;
	}

}