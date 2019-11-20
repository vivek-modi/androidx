/*
 * Copyright 2019 The Android Open Source Project
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

package androidx.fragment.app

import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.test.EmptyFragmentTestActivity
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.filters.SmallTest
import androidx.testutils.withActivity
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock

@RunWith(AndroidJUnit4::class)
@SmallTest
class FragmentStoreTest {

    private val dispatcher = FragmentLifecycleCallbacksDispatcher(
        mock(FragmentManager::class.java))

    private lateinit var fragmentStore: FragmentStore
    private lateinit var emptyFragment: Fragment
    private lateinit var emptyStateManager: FragmentStateManager

    @Before
    fun setup() {
        fragmentStore = FragmentStore()
        emptyFragment = StrictFragment()
        emptyStateManager = FragmentStateManager(dispatcher, emptyFragment)
    }

    @Test
    fun testMakeActive() {
        fragmentStore.makeActive(emptyStateManager)
        assertThat(fragmentStore.activeFragments)
            .containsExactly(emptyFragment)
        assertThat(fragmentStore.activeFragmentCount)
            .isEqualTo(1)
    }

    @Test
    fun testContainsActiveFragment() {
        fragmentStore.makeActive(emptyStateManager)
        assertThat(fragmentStore.containsActiveFragment(emptyFragment.mWho))
            .isTrue()
    }

    @Test
    fun testGetFragmentStateManager() {
        fragmentStore.makeActive(emptyStateManager)
        assertThat(fragmentStore.getFragmentStateManager(emptyFragment.mWho))
            .isSameInstanceAs(emptyStateManager)
    }

    @Test
    fun testFindActiveFragment() {
        fragmentStore.makeActive(emptyStateManager)
        assertThat(fragmentStore.findActiveFragment(emptyFragment.mWho))
            .isSameInstanceAs(emptyFragment)
    }

    @Test
    fun testMakeInactiveBurp() {
        fragmentStore.makeActive(emptyStateManager)
        assertThat(fragmentStore.activeFragments)
            .containsExactly(emptyFragment)

        fragmentStore.makeInactive(emptyStateManager)
        assertThat(fragmentStore.activeFragments)
            .containsExactly(null)

        fragmentStore.burpActive()
        assertThat(fragmentStore.activeFragments)
            .isEmpty()
    }

    @Test
    fun testResetActiveFragments() {
        fragmentStore.makeActive(emptyStateManager)
        assertThat(fragmentStore.activeFragments)
            .containsExactly(emptyFragment)

        fragmentStore.resetActiveFragments()
        assertThat(fragmentStore.activeFragments)
            .isEmpty()
    }

    @Test
    fun testSaveActiveFragments() {
        fragmentStore.makeActive(emptyStateManager)

        val savedActiveFragments = fragmentStore.saveActiveFragments()
        assertThat(savedActiveFragments)
            .hasSize(1)
        assertThat(savedActiveFragments[0].mWho)
            .isEqualTo(emptyFragment.mWho)
    }

    @Test
    fun testAddFragment() {
        fragmentStore.makeActive(emptyStateManager)
        assertThat(fragmentStore.fragments)
            .isEmpty()

        fragmentStore.addFragment(emptyFragment)
        assertThat(fragmentStore.fragments)
            .containsExactly(emptyFragment)
        assertThat(emptyFragment.mAdded)
            .isTrue()
    }

    @Test(expected = IllegalStateException::class)
    fun testAddAlreadyAddedFragment() {
        fragmentStore.makeActive(emptyStateManager)
        assertThat(fragmentStore.fragments)
            .isEmpty()

        fragmentStore.addFragment(emptyFragment)
        assertThat(fragmentStore.fragments)
            .containsExactly(emptyFragment)

        // Now add the Fragment again, triggering the IllegalStateException
        fragmentStore.addFragment(emptyFragment)
    }

    @Test
    fun testRestoreAddedFragments() {
        fragmentStore.makeActive(emptyStateManager)
        assertThat(fragmentStore.fragments)
            .isEmpty()

        val added = listOf(emptyFragment.mWho)
        fragmentStore.restoreAddedFragments(added)
        assertThat(fragmentStore.fragments)
            .containsExactly(emptyFragment)
        assertThat(emptyFragment.mAdded)
            .isTrue()
    }

    @Test
    fun testRemoveFragment() {
        fragmentStore.makeActive(emptyStateManager)
        assertThat(fragmentStore.fragments)
            .isEmpty()

        fragmentStore.addFragment(emptyFragment)
        assertThat(fragmentStore.fragments)
            .containsExactly(emptyFragment)
        assertThat(emptyFragment.mAdded)
            .isTrue()

        fragmentStore.removeFragment(emptyFragment)
        assertThat(fragmentStore.fragments)
            .isEmpty()
        assertThat(emptyFragment.mAdded)
            .isFalse()
    }

    @Test
    fun testSaveAddedFragments() {
        fragmentStore.makeActive(emptyStateManager)
        assertThat(fragmentStore.fragments)
            .isEmpty()

        fragmentStore.addFragment(emptyFragment)
        assertThat(fragmentStore.fragments)
            .containsExactly(emptyFragment)

        val savedAddedFragments = fragmentStore.saveAddedFragments()
        assertThat(savedAddedFragments)
            .containsExactly(emptyFragment.mWho)
    }

    @Test
    fun testFindFragmentById() {
        val id = 1
        emptyFragment.mFragmentId = id
        fragmentStore.makeActive(emptyStateManager)

        val foundFragment = fragmentStore.findFragmentById(id)
        assertThat(foundFragment)
            .isSameInstanceAs(emptyFragment)
    }

    @Test
    fun testFindFragmentByIdPrefersAdded() {
        val id = 1
        emptyFragment.mFragmentId = id
        fragmentStore.makeActive(emptyStateManager)

        val addedFragment: Fragment = StrictFragment()
        addedFragment.mFragmentId = id
        val addedStateManager = FragmentStateManager(dispatcher, addedFragment)
        fragmentStore.makeActive(addedStateManager)
        fragmentStore.addFragment(addedFragment)

        val foundFragment = fragmentStore.findFragmentById(id)
        assertThat(foundFragment)
            .isSameInstanceAs(addedFragment)
    }

    @Test
    fun testFindFragmentByTag() {
        val tag = "tag"
        emptyFragment.mTag = tag
        fragmentStore.makeActive(emptyStateManager)

        val foundFragment = fragmentStore.findFragmentByTag(tag)
        assertThat(foundFragment)
            .isSameInstanceAs(emptyFragment)
    }

    @Test
    fun testFindFragmentByTagPrefersAdded() {
        val tag = "tag"
        emptyFragment.mTag = tag
        fragmentStore.makeActive(emptyStateManager)

        val addedFragment: Fragment = StrictFragment()
        addedFragment.mTag = tag
        val addedStateManager = FragmentStateManager(dispatcher, addedFragment)
        fragmentStore.makeActive(addedStateManager)
        fragmentStore.addFragment(addedFragment)

        val foundFragment = fragmentStore.findFragmentByTag(tag)
        assertThat(foundFragment)
            .isSameInstanceAs(addedFragment)
    }

    @Test
    fun testFindFragmentByWho() {
        fragmentStore.makeActive(emptyStateManager)

        val foundFragment = fragmentStore.findFragmentByWho(emptyFragment.mWho)
        assertThat(foundFragment)
            .isSameInstanceAs(emptyFragment)
    }

    /**
     * As we don't have a mechanism for adding child fragments without a real, attached
     * FragmentManager, we fake it by using a real FragmentManager, then placing that
     * Fragment in our test FragmentStore in order to test that findFragmentByWho correctly
     * looks at child fragments.
     */
    @LargeTest
    @Test
    fun testFindFragmentByWhoChildFragment() {
        with(ActivityScenario.launch(EmptyFragmentTestActivity::class.java)) {
            val fm = withActivity { supportFragmentManager }
            val parentFragment = StrictFragment()
            fm.beginTransaction()
                .add(parentFragment, "parent")
                .commit()
            executePendingTransactions()
            val childFragment: Fragment = StrictFragment()
            parentFragment.childFragmentManager.beginTransaction()
                .add(childFragment, "child")
                .commit()
            executePendingTransactions(parentFragment.childFragmentManager)

            // Now fake that the parent Fragment is actually attached to our FragmentStore
            val parentStateManager = FragmentStateManager(dispatcher, parentFragment)
            fragmentStore.makeActive(parentStateManager)
            fragmentStore.addFragment(parentFragment)

            val foundFragment = fragmentStore.findFragmentByWho(childFragment.mWho)
            assertThat(foundFragment)
                .isSameInstanceAs(childFragment)
        }
    }

    @LargeTest
    @Test
    fun testFindFragmentUnder() {
        val container = mock(ViewGroup::class.java)
        emptyFragment.mView = mock(View::class.java)
        emptyFragment.mContainer = container
        fragmentStore.makeActive(emptyStateManager)
        fragmentStore.addFragment(emptyFragment)

        val onTopFragment: Fragment = StrictFragment()
        onTopFragment.mView = mock(View::class.java)
        onTopFragment.mContainer = container
        val onTopStateManager = FragmentStateManager(dispatcher, onTopFragment)
        fragmentStore.makeActive(onTopStateManager)
        fragmentStore.addFragment(onTopFragment)

        val underEmptyFragment = fragmentStore.findFragmentUnder(emptyFragment)
        assertThat(underEmptyFragment)
            .isNull()

        val underOnTopFragment = fragmentStore.findFragmentUnder(onTopFragment)
        assertThat(underOnTopFragment)
            .isSameInstanceAs(emptyFragment)
    }
}
