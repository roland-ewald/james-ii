/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.random.reproducible;

import java.nio.ByteBuffer;

/**
 * Default implementation for {@link ISeedPolicy}. It creates new {@link Seed}s
 * by multiple rehashes of the base seed with the provided identifier which is
 * in returned the source for the generated seed. It works best with
 * {@link Seed}s having a multiple of 4 {@link Seed#getSeedLength()}.
 * 
 * @author Stefan Rybacki
 * 
 */
public final class SimpleSeedPolicy implements ISeedPolicy {

  @Override
  public Seed createSeed(Seed from, String withName) {
    byte[] to = new byte[from.getSeedLength()];

    ByteBuffer src = ByteBuffer.wrap(from.getValue());
    ByteBuffer dst = ByteBuffer.wrap(to);

    int hash = withName.hashCode();

    while (src.remaining() > 4) {
      int i = src.getInt();
      i = i * 31 + hash;
      dst.putInt(i);
    }

    dst.put(src);
    return new Seed(to);
  }

}
