/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.hudi.table.format.cow.vector.reader;

import org.apache.hudi.table.format.cow.vector.HeapArrayVector;
import org.apache.hudi.table.format.cow.vector.HeapMapColumnVector;

import org.apache.flink.formats.parquet.vector.reader.ColumnReader;
import org.apache.flink.table.data.columnar.vector.ColumnVector;
import org.apache.flink.table.data.columnar.vector.writable.WritableColumnVector;
import org.apache.flink.table.types.logical.LogicalType;
import org.apache.flink.table.types.logical.MapType;

import java.io.IOException;

/**
 * Map {@link ColumnReader}.
 */
public class MapColumnReader implements ColumnReader<WritableColumnVector> {

  private final LogicalType logicalType;
  private final ArrayColumnReader keyReader;
  private final ArrayColumnReader valueReader;

  public MapColumnReader(
      ArrayColumnReader keyReader, ArrayColumnReader valueReader, LogicalType logicalType) {
    this.keyReader = keyReader;
    this.valueReader = valueReader;
    this.logicalType = logicalType;
  }

  public void readBatch(int total, ColumnVector column) throws IOException {
    HeapMapColumnVector mapColumnVector = (HeapMapColumnVector) column;
    MapType mapType = (MapType) logicalType;
    // initialize 2 ListColumnVector for keys and values
    HeapArrayVector keyArrayColumnVector = new HeapArrayVector(total);
    HeapArrayVector valueArrayColumnVector = new HeapArrayVector(total);
    // read the keys and values
    keyReader.readToVector(total, keyArrayColumnVector);
    valueReader.readToVector(total, valueArrayColumnVector);

    // set the related attributes according to the keys and values
    mapColumnVector.setKeys(keyArrayColumnVector.child);
    mapColumnVector.setValues(valueArrayColumnVector.child);
    mapColumnVector.setOffsets(keyArrayColumnVector.offsets);
    mapColumnVector.setLengths(keyArrayColumnVector.lengths);
    mapColumnVector.setSize(keyArrayColumnVector.getSize());
    for (int i = 0; i < keyArrayColumnVector.getLen(); i++) {
      if (keyArrayColumnVector.isNullAt(i)) {
        mapColumnVector.setNullAt(i);
      }
    }
  }

  @Override
  public void readToVector(int readNumber, WritableColumnVector vector) throws IOException {
    readBatch(readNumber, vector);
  }
}

