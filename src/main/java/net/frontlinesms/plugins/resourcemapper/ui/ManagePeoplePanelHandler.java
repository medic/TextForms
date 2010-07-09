/*
 * FrontlineSMS <http://www.frontlinesms.com>
 * Copyright 2007, 2008 kiwanja
 * 
 * This file is part of FrontlineSMS.
 * 
 * FrontlineSMS is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 * 
 * FrontlineSMS is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with FrontlineSMS. If not, see <http://www.gnu.org/licenses/>.
 */
package net.frontlinesms.plugins.resourcemapper.ui;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import net.frontlinesms.FrontlineUtils;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;

/*
 * ManageFieldsPanelHandler
 * @author Dale Zak
 * 
 * see {@link "http://www.frontlinesms.net"} for more details. 
 * copyright owned by Kiwanja.net
 */
public class ManagePeoplePanelHandler implements ThinletUiEventHandler {
	
	private static Logger LOG = FrontlineUtils.getLogger(BrowseDataPanelHandler.class);
	private static final String PANEL_XML = "/ui/plugins/resourcemapper/managePeoplePanel.xml";
	
	private UiGeneratorController ui;
	private ApplicationContext appContext;
	
	private Object mainPanel;
	
	public ManagePeoplePanelHandler(UiGeneratorController ui, ApplicationContext appContext) {
		LOG.debug("ManagePeoplePanelHandler");
		this.ui = ui;
		this.appContext = appContext;
		this.mainPanel = this.ui.loadComponentFromFile(PANEL_XML, this);
	}
	
	public Object getMainPanel() {
		return this.mainPanel;
	}
	
	public void addPerson() {
		System.out.println("addPerson");
	}
	
	public void editPerson() {
		System.out.println("editPerson");
	}
	
	public void deletePerson() {
		LOG.debug("deletePerson");
	}
	
	public void searchPeople() {
		LOG.debug("searchPeople");
	}
	
	public void viewResponses() {
		LOG.debug("viewResponses");
	}
}
