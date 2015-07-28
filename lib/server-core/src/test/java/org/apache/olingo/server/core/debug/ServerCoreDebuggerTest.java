/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.server.core.debug;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.olingo.commons.api.ODataRuntimeException;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.debug.DebugInformation;
import org.apache.olingo.server.api.debug.DebugSupport;
import org.junit.Before;
import org.junit.Test;

public class ServerCoreDebuggerTest {

  private ServerCoreDebugger debugger;

  @Before
  public void setupDebugger() {
    debugger = new ServerCoreDebugger(OData.newInstance());
    debugger.setDebugSupportProcessor(new LocalDebugProcessor());
  }

  @Test
  public void standardIsDebugModeIsFlase() {
    assertFalse(debugger.isDebugMode());
  }

  @Test
  public void resolveDebugModeNoDebugSupportProcessor() {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getParameter(DebugSupport.ODATA_DEBUG_QUERY_PARAMETER)).thenReturn(DebugSupport.ODATA_DEBUG_JSON);

    ServerCoreDebugger localDebugger = new ServerCoreDebugger(OData.newInstance());
    localDebugger.resolveDebugMode(request);
    assertFalse(debugger.isDebugMode());
  }

  @Test
  public void resolveDebugModeNullParameter() {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getParameter(DebugSupport.ODATA_DEBUG_QUERY_PARAMETER)).thenReturn(null);
    debugger.resolveDebugMode(request);
    assertFalse(debugger.isDebugMode());
  }

  @Test
  public void resolveDebugModeJsonNotAuthorized() {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getParameter(DebugSupport.ODATA_DEBUG_QUERY_PARAMETER)).thenReturn(DebugSupport.ODATA_DEBUG_JSON);

    DebugSupport debugSupportMock = mock(DebugSupport.class);
    when(debugSupportMock.isUserAuthorized()).thenReturn(false);

    ServerCoreDebugger localDebugger = new ServerCoreDebugger(OData.newInstance());
    localDebugger.setDebugSupportProcessor(debugSupportMock);

    localDebugger.resolveDebugMode(request);
    assertFalse(debugger.isDebugMode());
  }

  @Test
  public void testFailResponse() throws IOException {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getParameter(DebugSupport.ODATA_DEBUG_QUERY_PARAMETER)).thenReturn(DebugSupport.ODATA_DEBUG_JSON);
    debugger.resolveDebugMode(request);
    ODataResponse debugResponse = debugger.createDebugResponse(null, null, null, null, null, null);
    assertEquals(500, debugResponse.getStatusCode());
    assertEquals("ODataLibrary: Could not assemble debug response.", IOUtils.toString(debugResponse.getContent()));
  }

  @Test
  public void noDebugModeCreateDebugResponseCallMustDoNothing() {
    ODataResponse odResponse = new ODataResponse();
    ODataResponse debugResponse = debugger.createDebugResponse(null, null, null, odResponse, null, null);

    assertTrue(odResponse == debugResponse);
  }

  public class LocalDebugProcessor implements DebugSupport {

    @Override
    public void init(OData odata) {}

    @Override
    public boolean isUserAuthorized() {
      return true;
    }

    @Override
    public ODataResponse createDebugResponse(String debugFormat, DebugInformation debugInfo) {
      throw new ODataRuntimeException("Test");
    }
  }
}
