CREATE KEYSPACE IF NOT EXISTS todo WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1'}  AND durable_writes = true;

USE todo;
