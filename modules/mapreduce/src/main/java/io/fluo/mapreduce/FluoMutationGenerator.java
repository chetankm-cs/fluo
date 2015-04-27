/*
 * Copyright 2014 Fluo authors (see AUTHORS)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package io.fluo.mapreduce;

import java.nio.charset.StandardCharsets;

import org.apache.accumulo.core.client.BatchWriter;

import io.fluo.accumulo.util.ColumnConstants;
import io.fluo.accumulo.values.WriteValue;
import io.fluo.api.data.Bytes;
import io.fluo.api.data.Column;
import io.fluo.core.util.ByteUtil;
import io.fluo.core.util.Flutation;
import org.apache.accumulo.core.client.mapreduce.AccumuloOutputFormat;
import org.apache.accumulo.core.data.Mutation;
import org.apache.hadoop.io.Text;

/**
 * This class generates Accumulo mutations that are in the Fluo data format. This class is intended
 * to be used with {@link AccumuloOutputFormat} or {@link BatchWriter} inorder to seed an
 * initialized Fluo table on which no transactions have executed.
 */

public class FluoMutationGenerator {

  private Mutation mutation;

  /**
   * Creates builder for given row
   * 
   * @param row Will be encoded using UTF-8
   */
  public FluoMutationGenerator(CharSequence row) {
    mutation = new Mutation(row);
  }

  public FluoMutationGenerator(Text row) {
    mutation = new Mutation(row);
  }

  public FluoMutationGenerator(Bytes row) {
    mutation = new Mutation(row.toArray());
  }

  public FluoMutationGenerator(byte[] row) {
    mutation = new Mutation(row);
  }

  /**
   * Puts value at given column
   * 
   * @param value Will be encoded using UTF-8
   */
  public FluoMutationGenerator put(Column col, CharSequence value) {
    return put(col, value.toString().getBytes(StandardCharsets.UTF_8));
  }

  public FluoMutationGenerator put(Column col, Text value) {
    return put(col, ByteUtil.toBytes(value));
  }

  public FluoMutationGenerator put(Column col, Bytes value) {
    return put(col, value.toArray());
  }

  public FluoMutationGenerator put(Column col, byte[] value) {
    Flutation.put(mutation, col, ColumnConstants.DATA_PREFIX | 0, value);
    Flutation.put(mutation, col, ColumnConstants.WRITE_PREFIX | 1,
        WriteValue.encode(0, false, false));
    return this;
  }

  public Mutation build() {
    Mutation ret = mutation;
    mutation = null;
    return ret;
  }
}