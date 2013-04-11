/* ====================================================================
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
==================================================================== */

package org.apache.poi.poifs.storage;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import junit.framework.TestCase;

import org.apache.poi.poifs.common.POIFSConstants;
import org.apache.poi.poifs.property.PropertyTable;
import org.apache.poi.poifs.property.RootProperty;

/**
 * Class to test SmallBlockTableReader functionality
 *
 * @author Marc Johnson
 */
public final class TestSmallBlockTableReader extends TestCase {

	public void testReadingConstructor() throws IOException {

		// first, we need the raw data blocks
		String[] raw_data_array = {
			"52 00 6F 00 6F 00 74 00 20 00 45 00 6E 00 74 00 72 00 79 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"16 00 05 01 FF FF FF FF FF FF FF FF 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 0A 00 00 00 80 07 00 00 00 00 00 00",
			"44 00 65 00 61 00 6C 00 20 00 49 00 6E 00 66 00 6F 00 72 00 6D 00 61 00 74 00 69 00 6F 00 6E 00",
			"00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"22 00 01 01 FF FF FF FF FF FF FF FF 15 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"46 00 55 00 44 00 20 00 47 00 72 00 69 00 64 00 20 00 49 00 6E 00 66 00 6F 00 72 00 6D 00 61 00",
			"74 00 69 00 6F 00 6E 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"2A 00 02 01 FF FF FF FF 0E 00 00 00 FF FF FF FF 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"44 00 6F 00 75 00 62 00 6C 00 65 00 20 00 44 00 65 00 61 00 6C 00 69 00 6E 00 67 00 20 00 49 00",
			"6E 00 64 00 69 00 63 00 61 00 74 00 6F 00 72 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"32 00 02 01 FF FF FF FF 09 00 00 00 FF FF FF FF 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 04 00 00 00 00 00 00 00",
			"43 00 68 00 69 00 6C 00 64 00 20 00 50 00 65 00 72 00 63 00 65 00 6E 00 74 00 61 00 67 00 65 00",
			"20 00 50 00 65 00 72 00 6D 00 69 00 74 00 74 00 65 00 64 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"36 00 02 01 FF FF FF FF 07 00 00 00 FF FF FF FF 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 04 00 00 00 00 00 00 00",
			"43 00 61 00 6E 00 63 00 65 00 6C 00 6C 00 61 00 74 00 69 00 6F 00 6E 00 20 00 46 00 65 00 65 00",
			"20 00 46 00 69 00 78 00 65 00 64 00 20 00 56 00 61 00 6C 00 75 00 65 00 00 00 00 00 00 00 00 00",
			"3A 00 02 01 FF FF FF FF 06 00 00 00 FF FF FF FF 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 02 00 00 00 04 00 00 00 00 00 00 00",
			"55 00 6D 00 62 00 72 00 65 00 6C 00 6C 00 61 00 20 00 4C 00 69 00 6E 00 6B 00 73 00 20 00 61 00",
			"6E 00 64 00 20 00 50 00 61 00 73 00 73 00 65 00 6E 00 67 00 65 00 72 00 73 00 00 00 00 00 00 00",
			"3C 00 02 01 FF FF FF FF FF FF FF FF FF FF FF FF 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"43 00 61 00 6E 00 63 00 65 00 6C 00 6C 00 61 00 74 00 69 00 6F 00 6E 00 20 00 46 00 65 00 65 00",
			"20 00 50 00 65 00 72 00 63 00 65 00 6E 00 74 00 61 00 67 00 65 00 00 00 00 00 00 00 00 00 00 00",
			"38 00 02 01 FF FF FF FF 05 00 00 00 FF FF FF FF 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 03 00 00 00 04 00 00 00 00 00 00 00",
			"49 00 6E 00 66 00 61 00 6E 00 74 00 20 00 44 00 69 00 73 00 63 00 6F 00 75 00 6E 00 74 00 20 00",
			"50 00 65 00 72 00 6D 00 69 00 74 00 74 00 65 00 64 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"34 00 02 01 FF FF FF FF 04 00 00 00 FF FF FF FF 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 04 00 00 00 04 00 00 00 00 00 00 00",
			"43 00 61 00 6E 00 63 00 65 00 6C 00 6C 00 61 00 74 00 69 00 6F 00 6E 00 20 00 46 00 65 00 65 00",
			"20 00 43 00 75 00 72 00 72 00 65 00 6E 00 63 00 79 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"34 00 02 01 FF FF FF FF 08 00 00 00 FF FF FF FF 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 05 00 00 00 07 00 00 00 00 00 00 00",
			"4F 00 75 00 74 00 62 00 6F 00 75 00 6E 00 64 00 20 00 54 00 72 00 61 00 76 00 65 00 6C 00 20 00",
			"44 00 61 00 74 00 65 00 73 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"2C 00 02 01 FF FF FF FF 0B 00 00 00 FF FF FF FF 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 06 00 00 00 21 00 00 00 00 00 00 00",
			"42 00 75 00 73 00 69 00 6E 00 65 00 73 00 73 00 20 00 4A 00 75 00 73 00 74 00 69 00 66 00 69 00",
			"63 00 61 00 74 00 69 00 6F 00 6E 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"2E 00 02 01 FF FF FF FF 03 00 00 00 FF FF FF FF 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 07 00 00 00 04 00 00 00 00 00 00 00",
			"49 00 6E 00 66 00 61 00 6E 00 74 00 20 00 44 00 69 00 73 00 63 00 6F 00 75 00 6E 00 74 00 20 00",
			"56 00 61 00 6C 00 75 00 65 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"2C 00 02 01 FF FF FF FF 0D 00 00 00 FF FF FF FF 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 08 00 00 00 04 00 00 00 00 00 00 00",
			"4F 00 74 00 68 00 65 00 72 00 20 00 43 00 61 00 72 00 72 00 69 00 65 00 72 00 20 00 53 00 65 00",
			"63 00 74 00 6F 00 72 00 73 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"2C 00 02 01 FF FF FF FF 0A 00 00 00 FF FF FF FF 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 09 00 00 00 04 00 00 00 00 00 00 00",
			"4E 00 75 00 6D 00 62 00 65 00 72 00 20 00 6F 00 66 00 20 00 50 00 61 00 73 00 73 00 65 00 6E 00",
			"67 00 65 00 72 00 73 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"2A 00 02 01 FF FF FF FF 0C 00 00 00 FF FF FF FF 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 0A 00 00 00 04 00 00 00 00 00 00 00",
			"53 00 61 00 6C 00 65 00 73 00 20 00 41 00 72 00 65 00 61 00 20 00 43 00 6F 00 64 00 65 00 00 00",
			"00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"20 00 02 01 1C 00 00 00 FF FF FF FF FF FF FF FF 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 0B 00 00 00 04 00 00 00 00 00 00 00",
			"4F 00 74 00 68 00 65 00 72 00 20 00 52 00 65 00 66 00 75 00 6E 00 64 00 20 00 54 00 65 00 78 00",
			"74 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"24 00 02 01 17 00 00 00 FF FF FF FF FF FF FF FF 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 0C 00 00 00 04 00 00 00 00 00 00 00",
			"4D 00 61 00 78 00 69 00 6D 00 75 00 6D 00 20 00 53 00 74 00 61 00 79 00 20 00 50 00 65 00 72 00",
			"69 00 6F 00 64 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"28 00 02 01 FF FF FF FF 14 00 00 00 FF FF FF FF 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 0D 00 00 00 04 00 00 00 00 00 00 00",
			"4E 00 65 00 74 00 20 00 52 00 65 00 6D 00 69 00 74 00 20 00 50 00 65 00 72 00 6D 00 69 00 74 00",
			"74 00 65 00 64 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"28 00 02 01 FF FF FF FF 13 00 00 00 FF FF FF FF 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 0E 00 00 00 04 00 00 00 00 00 00 00",
			"50 00 65 00 72 00 63 00 65 00 6E 00 74 00 61 00 67 00 65 00 20 00 6F 00 66 00 20 00 59 00 69 00",
			"65 00 6C 00 64 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"28 00 02 01 FF FF FF FF 02 00 00 00 FF FF FF FF 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 0F 00 00 00 04 00 00 00 00 00 00 00",
			"4E 00 61 00 74 00 75 00 72 00 65 00 20 00 6F 00 66 00 20 00 56 00 61 00 72 00 69 00 61 00 74 00",
			"69 00 6F 00 6E 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"28 00 02 01 FF FF FF FF 12 00 00 00 FF FF FF FF 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 10 00 00 00 50 00 00 00 00 00 00 00",
			"46 00 55 00 44 00 20 00 47 00 72 00 69 00 64 00 20 00 44 00 69 00 6D 00 65 00 6E 00 73 00 69 00",
			"6F 00 6E 00 73 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"28 00 02 01 10 00 00 00 11 00 00 00 FF FF FF FF 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 12 00 00 00 04 00 00 00 00 00 00 00",
			"44 00 65 00 61 00 6C 00 20 00 44 00 65 00 73 00 63 00 72 00 69 00 70 00 74 00 69 00 6F 00 6E 00",
			"00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"22 00 02 01 19 00 00 00 FF FF FF FF FF FF FF FF 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 13 00 00 00 09 00 00 00 00 00 00 00",
			"54 00 52 00 56 00 41 00 20 00 49 00 6E 00 66 00 6F 00 72 00 6D 00 61 00 74 00 69 00 6F 00 6E 00",
			"00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"22 00 02 01 18 00 00 00 FF FF FF FF FF FF FF FF 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 14 00 00 00 04 00 00 00 00 00 00 00",
			"50 00 72 00 6F 00 72 00 61 00 74 00 65 00 20 00 43 00 6F 00 6D 00 6D 00 65 00 6E 00 74 00 73 00",
			"00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"22 00 02 01 16 00 00 00 FF FF FF FF FF FF FF FF 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"43 00 6F 00 6D 00 6D 00 69 00 73 00 73 00 69 00 6F 00 6E 00 20 00 56 00 61 00 6C 00 75 00 65 00",
			"00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"22 00 02 01 0F 00 00 00 FF FF FF FF FF FF FF FF 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 15 00 00 00 04 00 00 00 00 00 00 00",
			"4D 00 61 00 78 00 69 00 6D 00 75 00 6D 00 20 00 53 00 74 00 61 00 79 00 00 00 00 00 00 00 00 00",
			"00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"1A 00 02 01 20 00 00 00 FF FF FF FF FF FF FF FF 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 16 00 00 00 05 00 00 00 00 00 00 00",
			"44 00 65 00 61 00 6C 00 20 00 43 00 75 00 72 00 72 00 65 00 6E 00 63 00 79 00 00 00 00 00 00 00",
			"00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"1C 00 02 01 1D 00 00 00 FF FF FF FF FF FF FF FF 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 17 00 00 00 07 00 00 00 00 00 00 00",
			"43 00 6F 00 6E 00 73 00 6F 00 72 00 74 00 69 00 61 00 20 00 43 00 6F 00 64 00 65 00 73 00 00 00",
			"00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"20 00 02 01 1B 00 00 00 FF FF FF FF FF FF FF FF 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"42 00 75 00 73 00 69 00 6E 00 65 00 73 00 73 00 20 00 54 00 79 00 70 00 65 00 00 00 00 00 00 00",
			"00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"1C 00 02 01 1A 00 00 00 FF FF FF FF FF FF FF FF 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 18 00 00 00 04 00 00 00 00 00 00 00",
			"44 00 65 00 61 00 6C 00 20 00 54 00 79 00 70 00 65 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"14 00 02 01 23 00 00 00 FF FF FF FF FF FF FF FF 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 19 00 00 00 04 00 00 00 00 00 00 00",
			"53 00 75 00 72 00 63 00 68 00 61 00 72 00 67 00 65 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"14 00 02 01 21 00 00 00 FF FF FF FF FF FF FF FF 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 1A 00 00 00 04 00 00 00 00 00 00 00",
			"41 00 67 00 65 00 6E 00 74 00 73 00 20 00 4E 00 61 00 6D 00 65 00 00 00 00 00 00 00 00 00 00 00",
			"00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"18 00 02 01 1F 00 00 00 FF FF FF FF FF FF FF FF 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 1B 00 00 00 04 00 00 00 00 00 00 00",
			"46 00 61 00 72 00 65 00 20 00 54 00 79 00 70 00 65 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"14 00 02 01 1E 00 00 00 FF FF FF FF FF FF FF FF 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 1C 00 00 00 04 00 00 00 00 00 00 00",
			"53 00 75 00 62 00 20 00 44 00 65 00 61 00 6C 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"12 00 02 01 24 00 00 00 FF FF FF FF FF FF FF FF 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 1D 00 00 00 04 00 00 00 00 00 00 00",
			"41 00 4C 00 43 00 20 00 43 00 6F 00 64 00 65 00 73 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"14 00 02 01 22 00 00 00 FF FF FF FF FF FF FF FF 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"52 00 65 00 6D 00 61 00 72 00 6B 00 73 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"10 00 02 01 FF FF FF FF FF FF FF FF FF FF FF FF 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"02 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"02 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"02 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
			"02 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"02 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"08 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"08 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"02 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"08 00 03 00 47 42 50 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"08 00 1D 00 28 41 29 31 36 2D 4F 63 74 2D 32 30 30 31 20 74 6F 20 31 36 2D 4F 63 74 2D 32 30 30",
			"31 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"08 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"08 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"02 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"08 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"08 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"08 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"02 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"02 00 01 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"08 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"02 00 00 00 08 00 00 00 02 00 00 00 08 00 00 00 02 00 00 00 08 00 00 00 02 00 00 00 08 00 00 00",
			"02 00 00 00 08 00 00 00 02 00 00 00 08 00 00 00 02 00 00 00 08 00 00 00 02 00 00 00 08 00 00 00",
			"02 00 00 00 08 00 00 00 02 00 00 00 08 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"02 00 18 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"08 00 05 00 6A 61 6D 65 73 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"02 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"08 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"08 00 01 00 31 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"08 00 03 00 47 42 50 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"08 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"02 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"08 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"08 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"08 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"02 00 00 00 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"FE FF FF FF FE FF FF FF FE FF FF FF FE FF FF FF FE FF FF FF FE FF FF FF FE FF FF FF FE FF FF FF",
			"FE FF FF FF FE FF FF FF FE FF FF FF FE FF FF FF FE FF FF FF FE FF FF FF FE FF FF FF FE FF FF FF",
			"11 00 00 00 FE FF FF FF FE FF FF FF FE FF FF FF FE FF FF FF FE FF FF FF FE FF FF FF FE FF FF FF",
			"FE FF FF FF FE FF FF FF FE FF FF FF FE FF FF FF FE FF FF FF FE FF FF FF FF FF FF FF FF FF FF FF",
			"FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"01 00 00 00 02 00 00 00 03 00 00 00 04 00 00 00 05 00 00 00 06 00 00 00 07 00 00 00 08 00 00 00",
			"09 00 00 00 FE FF FF FF 0B 00 00 00 0C 00 00 00 0D 00 00 00 FE FF FF FF FE FF FF FF FE FF FF FF",
			"FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
			"FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF",
		};

		RawDataBlockList data_blocks = new RawDataBlockList(new ByteArrayInputStream(RawDataUtil
				.decode(raw_data_array)), POIFSConstants.SMALLER_BIG_BLOCK_SIZE_DETAILS);
		int[] bat_array = { 15 };

		// need to initialize the block list with a block allocation
		// table
		new BlockAllocationTableReader(POIFSConstants.SMALLER_BIG_BLOCK_SIZE_DETAILS, 1, bat_array, 0, -2, data_blocks);

		// get property table from the document
		PropertyTable properties = new PropertyTable(POIFSConstants.SMALLER_BIG_BLOCK_SIZE_DETAILS, 0, data_blocks);
		RootProperty root = properties.getRoot();
		BlockList bl = SmallBlockTableReader.getSmallDocumentBlocks(
		      POIFSConstants.SMALLER_BIG_BLOCK_SIZE_DETAILS, data_blocks, root, 14);
		assertNotNull(bl);
	}
}
