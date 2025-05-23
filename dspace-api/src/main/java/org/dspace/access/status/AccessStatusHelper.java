/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.access.status;

import java.sql.SQLException;
import java.time.LocalDate;

import org.dspace.content.AccessStatus;
import org.dspace.content.Bitstream;
import org.dspace.content.Item;
import org.dspace.core.Context;

/**
 * Plugin interface for the access status calculation.
 */
public interface AccessStatusHelper {
    /**
     * Calculate the access status for the item.
     *
     * @param context the DSpace context
     * @param item the item
     * @param threshold the embargo threshold date
     * @param type the type of calculation
     * @return the access status
     * @throws SQLException An exception that provides information on a database access error or other errors.
     */
    public AccessStatus getAccessStatusFromItem(Context context,
        Item item, LocalDate threshold, String type) throws SQLException;

    /**
     * Calculate the anonymous access status for the item.
     *
     * @param context the DSpace context
     * @param item the item to check for embargo information
     * @param threshold the embargo threshold date
     * @return the access status
     * @throws SQLException An exception that provides information on a database access error or other errors.
     */
    public AccessStatus getAnonymousAccessStatusFromItem(Context context,
        Item item, LocalDate threshold) throws SQLException;

    /**
     * Calculate the access status for the bitstream.
     *
     * @param context the DSpace context
     * @param bitstream the bitstream
     * @param threshold the embargo threshold date
     * @param type the type of calculation
     * @return the access status
     * @throws SQLException An exception that provides information on a database access error or other errors.
     */
    public AccessStatus getAccessStatusFromBitstream(Context context,
        Bitstream bitstream, LocalDate threshold, String type) throws SQLException;
}
