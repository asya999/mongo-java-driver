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

package org.mongodb.operation;

import org.mongodb.CommandResult;
import org.mongodb.Document;
import org.mongodb.MongoNamespace;
import org.mongodb.WriteConcern;
import org.mongodb.codecs.DocumentCodec;
import org.mongodb.connection.BufferProvider;
import org.mongodb.protocol.DeleteProtocol;
import org.mongodb.session.PrimaryServerSelector;
import org.mongodb.session.ServerConnectionProvider;
import org.mongodb.session.ServerConnectionProviderOptions;
import org.mongodb.session.Session;

import java.util.Arrays;

import static org.mongodb.assertions.Assertions.notNull;

public class RemoveUserOperation extends BaseOperation<CommandResult> {
    private final String database;
    private final String userName;

    public RemoveUserOperation(final String database, final String userName, final BufferProvider bufferProvider,
                               final Session session, final boolean closeSession) {
        super(bufferProvider, session, closeSession);
        this.database = notNull("database", database);
        this.userName = notNull("userName", userName);
    }

    @Override
    public CommandResult execute() {
        ServerConnectionProvider serverConnectionProvider = getSession().createServerConnectionProvider(
                new ServerConnectionProviderOptions(false, new PrimaryServerSelector()));
        MongoNamespace namespace = new MongoNamespace(database, "system.users");
        DocumentCodec codec = new DocumentCodec();
        return new DeleteProtocol(namespace, WriteConcern.ACKNOWLEDGED,
                Arrays.asList(new Remove(WriteConcern.ACKNOWLEDGED, new Document("user", userName))), codec,
                getBufferProvider(), serverConnectionProvider.getServerDescription(), serverConnectionProvider.getConnection(),
                true).execute();
    }
}
