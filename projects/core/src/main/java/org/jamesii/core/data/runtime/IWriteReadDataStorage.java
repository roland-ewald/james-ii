/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.runtime;

import java.io.Serializable;

import org.jamesii.core.data.runtime.read.IReadDataStorage;
import org.jamesii.core.data.runtime.write.IWriteDataStorageSingleComputation;
import org.jamesii.core.data.storage.IDataStorage;

/**
 * Data storage interface providing read / write access based on the
 * {@link IReadDataStorage} and {@link IWriteDataStorageSingleComputation}
 * interfaces. <br/>
 * A data storage can only be used for ONE computation task at once (for
 * writing). <br/>
 * <table>
 * <tr>
 * <td>dataId</td>
 * <td>Identifies an object in the computation (comp alg or model). Can be
 * freely defined by models/ observers / ...</td>
 * </tr>
 * <tr>
 * <td>attribute</td>
 * <td>Each object identified by a dataId can have several attributes. String
 * values can be freely defined by the model / observers / ...</td>
 * </tr>
 * </table>
 * 
 * @author Jan Himmelspach
 * 
 */
public interface IWriteReadDataStorage<InternalID extends Serializable> extends
    IReadDataStorage<InternalID>, IWriteDataStorageSingleComputation,
    IDataStorage<InternalID> {

}
