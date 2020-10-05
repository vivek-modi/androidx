/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.wear.complications.data

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class TypeTest {
    @Test
    fun asWireComplicationType() {
        assertThatIsWireType(ComplicationType.NO_DATA, WireComplicationData.TYPE_NO_DATA)
        assertThatIsWireType(ComplicationType.EMPTY, WireComplicationData.TYPE_EMPTY)
        assertThatIsWireType(
            ComplicationType.NOT_CONFIGURED,
            WireComplicationData.TYPE_NOT_CONFIGURED
        )
        assertThatIsWireType(ComplicationType.SHORT_TEXT, WireComplicationData.TYPE_SHORT_TEXT)
        assertThatIsWireType(ComplicationType.LONG_TEXT, WireComplicationData.TYPE_LONG_TEXT)
        assertThatIsWireType(ComplicationType.RANGED_VALUE, WireComplicationData.TYPE_RANGED_VALUE)
        assertThatIsWireType(ComplicationType.MONOCHROMATIC_IMAGE, WireComplicationData.TYPE_ICON)
        assertThatIsWireType(ComplicationType.SMALL_IMAGE, WireComplicationData.TYPE_SMALL_IMAGE)
        assertThatIsWireType(
            ComplicationType.BACKGROUND_IMAGE,
            WireComplicationData.TYPE_LARGE_IMAGE
        )
        assertThatIsWireType(
            ComplicationType.NO_PERMISSION,
            WireComplicationData.TYPE_NO_PERMISSION
        )
    }

    private fun assertThatIsWireType(type: ComplicationType, wireType: Int) {
        assertThat(type.asWireComplicationType()).isEqualTo(wireType)
    }

    @Test
    fun fromWireType() {
        assertThatIsApiType(WireComplicationData.TYPE_NO_DATA, ComplicationType.NO_DATA)
        assertThatIsApiType(WireComplicationData.TYPE_EMPTY, ComplicationType.EMPTY)
        assertThatIsApiType(
            WireComplicationData.TYPE_NOT_CONFIGURED,
            ComplicationType.NOT_CONFIGURED
        )
        assertThatIsApiType(WireComplicationData.TYPE_SHORT_TEXT, ComplicationType.SHORT_TEXT)
        assertThatIsApiType(WireComplicationData.TYPE_LONG_TEXT, ComplicationType.LONG_TEXT)
        assertThatIsApiType(WireComplicationData.TYPE_RANGED_VALUE, ComplicationType.RANGED_VALUE)
        assertThatIsApiType(WireComplicationData.TYPE_ICON, ComplicationType.MONOCHROMATIC_IMAGE)
        assertThatIsApiType(WireComplicationData.TYPE_SMALL_IMAGE, ComplicationType.SMALL_IMAGE)
        assertThatIsApiType(
            WireComplicationData.TYPE_LARGE_IMAGE,
            ComplicationType.BACKGROUND_IMAGE
        )
        assertThatIsApiType(WireComplicationData.TYPE_NO_PERMISSION, ComplicationType.NO_PERMISSION)
    }

    private fun assertThatIsApiType(wireType: Int, type: ComplicationType) {
        assertThat(ComplicationType.fromWireType(wireType)).isEqualTo(type)
    }

    @Test
    fun fromUnknownWireType() {
        assertThat(ComplicationType.fromWireType(-1)).isEqualTo(ComplicationType.EMPTY)
        assertThat(ComplicationType.fromWireType(1000)).isEqualTo(ComplicationType.EMPTY)
    }
}