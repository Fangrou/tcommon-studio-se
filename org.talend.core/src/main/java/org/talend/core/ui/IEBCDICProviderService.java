// ============================================================================
//
// Copyright (C) 2006-2007 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.core.ui;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.ui.IWorkbench;
import org.talend.core.IService;
import org.talend.repository.model.RepositoryNode;

/**
 * ggu class global comment. Detailled comment
 */
public interface IEBCDICProviderService extends IService {

    public IWizard newEbcdicWizard(IWorkbench workbench, boolean creation, RepositoryNode node, String[] existingNames);
}
