/**
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
/*
 * Copyright 2005 Sun Microsystems, Inc. All rights reserved.
 */
/*
 * $Id: DOMEnvelopedTransform.java 1197150 2011-11-03 14:34:57Z coheigea $
 */
package org.apache.xml.dsig.internal.dom;

import java.security.InvalidAlgorithmParameterException;
import org.apache.xml.crypto.dsig.spec.TransformParameterSpec;

/**
 * DOM-based implementation of Enveloped Signature Transform.
 * (Uses Apache XML-Sec Transform implementation)
 *
 * @author Sean Mullan
 */
public final class DOMEnvelopedTransform extends ApacheTransform {

    public void init(TransformParameterSpec params)
        throws InvalidAlgorithmParameterException {
        if (params != null) {
            throw new InvalidAlgorithmParameterException("params must be null");
        }
    }
}
