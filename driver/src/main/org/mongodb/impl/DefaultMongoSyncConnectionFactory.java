/*
 * Copyright (c) 2008 - 2013 10gen, Inc. <http://10gen.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mongodb.impl;

import org.mongodb.MongoClientOptions;
import org.mongodb.MongoCredential;
import org.mongodb.MongoSyncConnectionFactory;
import org.mongodb.ServerAddress;
import org.mongodb.io.BufferPool;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;
import java.nio.ByteBuffer;
import java.util.List;

public class DefaultMongoSyncConnectionFactory implements MongoSyncConnectionFactory {
    private MongoClientOptions options;
    private ServerAddress serverAddress;
    private BufferPool<ByteBuffer> bufferPool;
    private List<MongoCredential> credentialList;

    public DefaultMongoSyncConnectionFactory(final MongoClientOptions options, final ServerAddress serverAddress,
                                             final BufferPool<ByteBuffer> bufferPool, final List<MongoCredential> credentialList) {
        this.options = options;
        this.serverAddress = serverAddress;
        this.bufferPool = bufferPool;
        this.credentialList = credentialList;
    }

    @Override
    public ServerAddress getServerAddress() {
        return serverAddress;
    }

    @Override
    public MongoSyncConnection create() {
        if (options.isSSLEnabled()) {
            return new DefaultMongoSocketConnection(serverAddress, bufferPool, credentialList, SSLSocketFactory.getDefault());
        }
        else if (System.getProperty("org.mongodb.useSocket", "false").equals("true")) {
            return new DefaultMongoSocketConnection(serverAddress, bufferPool, credentialList, SocketFactory.getDefault());
        }
        else {
            return new DefaultMongoSocketChannelConnection(serverAddress, bufferPool, credentialList);
        }
    }
}