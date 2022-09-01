/*
 * Copyright (c) 2022 gematik GmbH
 * 
 * Licensed under the Apache License, Version 2.0 (the License);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.gematik.pki.pkits.ocsp.responder.controllers;

import static de.gematik.pki.pkits.common.PkitsConstants.WEBSERVER_INFO_ENDPOINT;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.gematik.pki.pkits.ocsp.responder.data.OcspInfoRequestDto;
import de.gematik.pki.pkits.ocsp.responder.data.OcspRequestHistory;
import de.gematik.pki.pkits.ocsp.responder.data.OcspRequestHistoryEntryDto;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class OcspInfoController {

  private final OcspRequestHistory ocspRequestHistory;

  /**
   * @param request InfoRequest
   * @return An excerpt of the history of requests
   * @throws IOException in case of ServletInputStream problem
   */
  @PostMapping(value = WEBSERVER_INFO_ENDPOINT)
  public List<OcspRequestHistoryEntryDto> info(final HttpServletRequest request)
      throws IOException {

    final OcspInfoRequestDto ocspInfoRequest =
        new ObjectMapper().readValue(request.getInputStream(), OcspInfoRequestDto.class);
    final List<OcspRequestHistoryEntryDto> retList =
        getHistoryEntriesForPositiveCertSerialNumber(ocspInfoRequest);

    deleteHistoryOnDemand(ocspInfoRequest);

    return Collections.unmodifiableList(retList);
  }

  private List<OcspRequestHistoryEntryDto> getHistoryEntriesForPositiveCertSerialNumber(
      final OcspInfoRequestDto ocspInfoRequest) {
    final List<OcspRequestHistoryEntryDto> retList;
    if (ocspInfoRequest.getCertSerialNr().signum() == 1) {
      log.info("InfoRequest for certSerialNr : {} received.", ocspInfoRequest.getCertSerialNr());
      retList = ocspRequestHistory.getExcerpt(ocspInfoRequest.getCertSerialNr());
      log.info("Found history with {} entries.", retList.size());
    } else {
      retList = Collections.emptyList();
    }
    return retList;
  }

  private void deleteHistoryOnDemand(final OcspInfoRequestDto ocspInfoRequestDto) {
    switch (ocspInfoRequestDto.getHistoryDeleteOption()) {
      case DELETE_FULL_HISTORY -> {
        ocspRequestHistory.deleteAll();
        log.debug("OCSP request history: cleared");
      }
      case DELETE_CERT_HISTORY -> {
        ocspRequestHistory.deleteEntries(ocspInfoRequestDto.getCertSerialNr());
        log.debug(
            "OCSP request history: cleared certSerialNr {}", ocspInfoRequestDto.getCertSerialNr());
      }
      default -> log.debug("deleteHistoryOnDemand called without delete option.");
    }
  }
}
