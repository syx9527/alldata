/*
 * Datart
 * <p>
 * Copyright 2021
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package datart.server.base.params;

import datart.core.base.consts.ShareAuthenticationMode;
import datart.core.base.consts.ShareRowPermissionBy;
import datart.security.base.ResourceType;
import lombok.Data;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Set;

@Data
public class ShareCreateParam {

    @NotNull
    private ResourceType vizType;

    @NotNull
    private String vizId;

    @Future
    private Date expiryDate;

    @NotNull
    private ShareAuthenticationMode authenticationMode;

    private ShareRowPermissionBy rowPermissionBy;

    private Set<String> roles;

    private Set<String> users;

    private String authenticationCode;

}