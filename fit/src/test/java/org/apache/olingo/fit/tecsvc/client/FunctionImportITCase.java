/* 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.fit.tecsvc.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collections;
import java.util.List;

import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.invoke.ODataInvokeRequest;
import org.apache.olingo.client.api.communication.response.ODataInvokeResponse;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.commons.api.domain.ODataEntity;
import org.apache.olingo.commons.api.domain.ODataEntitySet;
import org.apache.olingo.commons.api.domain.ODataProperty;
import org.apache.olingo.commons.api.domain.ODataValue;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.fit.AbstractBaseTestITCase;
import org.apache.olingo.fit.tecsvc.TecSvcConst;
import org.junit.Test;

public class FunctionImportITCase extends AbstractBaseTestITCase {

  @Test
  public void entity() throws Exception {
    final ODataInvokeRequest<ODataEntity> request = getClient().getInvokeRequestFactory()
        .getFunctionInvokeRequest(getClient().newURIBuilder(TecSvcConst.BASE_URI)
            .appendOperationCallSegment("FICRTESTwoKeyNav").build(), ODataEntity.class);
    assertNotNull(request);

    final ODataInvokeResponse<ODataEntity> response = request.execute();
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    final ODataEntity entity = response.getBody();
    assertNotNull(entity);
    final ODataProperty property = entity.getProperty("PropertyInt16");
    assertNotNull(property);
    assertEquals(1, property.getPrimitiveValue().toValue());
  }

  @Test
  public void entityCollection() {
    final ODataInvokeRequest<ODataEntitySet> request = getClient().getInvokeRequestFactory()
        .getFunctionInvokeRequest(getClient().newURIBuilder(TecSvcConst.BASE_URI)
            .appendOperationCallSegment("FICRTCollESTwoKeyNavParam").build(), ODataEntitySet.class,
            Collections.<String, ODataValue> singletonMap("ParameterInt16",
                getClient().getObjectFactory().newPrimitiveValueBuilder().buildInt32(2)));
    assertNotNull(request);

    final ODataInvokeResponse<ODataEntitySet> response = request.execute();
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    final ODataEntitySet entitySet = response.getBody();
    assertNotNull(entitySet);
    final List<ODataEntity> entities = entitySet.getEntities();
    assertNotNull(entities);
    assertEquals(2, entities.size());
    final ODataEntity entity = entities.get(1);
    assertNotNull(entity);
    final ODataProperty property = entity.getProperty("PropertyString");
    assertNotNull(property);
    assertNotNull(property.getPrimitiveValue());
    assertEquals("2", property.getPrimitiveValue().toValue());
  }

  @Override
  protected ODataClient getClient() {
    ODataClient odata = ODataClientFactory.getClient();
    odata.getConfiguration().setDefaultPubFormat(ODataFormat.JSON);
    return odata;
  }
}
