/*
 * Copyright 2023 gematik GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.gematik.pki.pkits.ocsp.responder.data;

import java.math.BigInteger;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OcspInfoRequestDto {

  private Integer tslSeqNr;
  private BigInteger certSerialNr;
  private HistoryDeleteOption historyDeleteOption;

  public enum HistoryDeleteOption {
    DELETE_NOTHING,
    DELETE_QUERIED_HISTORY,
    DELETE_FULL_HISTORY
  }

  @Override
  public String toString() {
    return "OcspInfoRequestDto{tslSeqNr=%d, certSerialNr=%s, historyDeleteOption=%s}"
        .formatted(tslSeqNr, certSerialNr, historyDeleteOption);
  }
}
