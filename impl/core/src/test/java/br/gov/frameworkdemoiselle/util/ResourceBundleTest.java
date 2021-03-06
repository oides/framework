/*
 * Demoiselle Framework
 * Copyright (C) 2010 SERPRO
 * ----------------------------------------------------------------------------
 * This file is part of Demoiselle Framework.
 * 
 * Demoiselle Framework is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License version 3
 * as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License version 3
 * along with this program; if not,  see <http://www.gnu.org/licenses/>
 * or write to the Free Software Foundation, Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA  02110-1301, USA.
 * ----------------------------------------------------------------------------
 * Este arquivo é parte do Framework Demoiselle.
 * 
 * O Framework Demoiselle é um software livre; você pode redistribuí-lo e/ou
 * modificá-lo dentro dos termos da GNU LGPL versão 3 como publicada pela Fundação
 * do Software Livre (FSF).
 * 
 * Este programa é distribuído na esperança que possa ser útil, mas SEM NENHUMA
 * GARANTIA; sem uma garantia implícita de ADEQUAÇÃO a qualquer MERCADO ou
 * APLICAÇÃO EM PARTICULAR. Veja a Licença Pública Geral GNU/LGPL em português
 * para maiores detalhes.
 * 
 * Você deve ter recebido uma cópia da GNU LGPL versão 3, sob o título
 * "LICENCA.txt", junto com esse programa. Se não, acesse <http://www.gnu.org/licenses/>
 * ou escreva para a Fundação do Software Livre (FSF) Inc.,
 * 51 Franklin St, Fifth Floor, Boston, MA 02111-1301, USA.
 */
package br.gov.frameworkdemoiselle.util;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Enumeration;
import java.util.ListResourceBundle;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class ResourceBundleTest {

	/**
	 * This is a workaround to mock java.util.ResourceBundle. Since getString(key) method is defined as final, there is
	 * no way to extend and override it. For that reason, setting expectations (i.e. expect(...)) won't work.
	 */
	class MockResourceBundle extends ListResourceBundle {

		private Object[][] contents = new Object[][] { { "msgWithoutParams", "no params" },
				{ "msgWithParams", "params: {0}, {1}" } };

		protected Object[][] getContents() {
			return contents;
		}

	};

	private ResourceBundle resourceBundle;

	private java.util.ResourceBundle mockResourceBundle;

	@Before
	public void setUp() throws Exception {
		mockResourceBundle = new MockResourceBundle();
		resourceBundle = new ResourceBundle(mockResourceBundle);
	}

	@Test
	public void containsKey() {
		assertTrue(resourceBundle.containsKey("msgWithoutParams"));

		assertFalse(resourceBundle.containsKey("inexistentKey"));
	}

	@Test
	public void getKeys() {
		int keyCount = 0;

		Enumeration<String> e = resourceBundle.getKeys();

		while (e.hasMoreElements()) {
			keyCount++;
			e.nextElement();
		}

		assertEquals(resourceBundle.keySet().size(), keyCount);
	}

	@Test
	public void testGetLocale() {
		assertNull(resourceBundle.getLocale());
	}

	@Test
	public void testKeySet() {
		assertEquals(2, resourceBundle.keySet().size());
	}

	@Test
	public void getString() {
		assertEquals("no params", resourceBundle.getString("msgWithoutParams"));

		assertEquals("params: a, b", resourceBundle.getString("msgWithParams", "a", "b"));

		assertEquals("params: {0}, {1}", resourceBundle.getString("msgWithParams"));
	}

	/**
	 * For this test, java.util.ResourceBundle is mocked to force an exception. Since the getString method is called
	 * from the actual ResourceBundle, not from the mock, it tries to find a handleGetObject method that doesn't exist.
	 * 
	 * @throws Exception
	 */
	@Test(expected = RuntimeException.class)
	public void getStringWhenHandleGetObjectThrowsException() {
		mockResourceBundle = createMock(java.util.ResourceBundle.class);
		resourceBundle = new ResourceBundle(mockResourceBundle);

		replay(mockResourceBundle);

		resourceBundle.getString("msgWithParams");

		verify(mockResourceBundle);

		Assert.fail();
	}

}
