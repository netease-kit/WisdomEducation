package com.netease.yunxin.app.wisdom.education;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions.PositionableRecyclerViewAction;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import static com.netease.yunxin.integrationtest.library.IntegrationTest.TAG;

import com.netease.yunxin.integrationtest.library.uitest.UITestRegisterHelper;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withParentIndex;
import static androidx.test.espresso.matcher.ViewMatchers.withSubstring;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;


/**
 * Basic testing method encapsulation
 */
public final class BaseTestUtils {

    private static final long VIEW_ACTIONS_DELAY = 1500L;

    public static ViewInteraction viewAction(@IdRes int id, final ViewAction... viewActions) {
        waitForTime(VIEW_ACTIONS_DELAY);
        return onView(withId(id)).perform(viewActions);
    }

    public static ViewInteraction viewAction(String text, final ViewAction... viewActions) {
        waitForTime(VIEW_ACTIONS_DELAY);
        return onView(withText(text)).perform(viewActions);
    }

    public static ViewInteraction viewAction(@IdRes int id, String text, final ViewAction... viewActions) {
        waitForTime(VIEW_ACTIONS_DELAY);
        return onView(allOf(withId(id), withText(text))).perform(viewActions);
    }

    public static ViewInteraction viewAction(Matcher<View> viewMatcher, final ViewAction... viewActions) {
        waitForTime(VIEW_ACTIONS_DELAY);
        return onView(viewMatcher).perform(viewActions);
    }

    /**
     * Input the text of the target textbox control
     *
     * @param id      The ID of the target control
     * @param content The text of the target control
     * @return View Interaction
     */
    public static ViewInteraction inputText(@IdRes int id, String content) {
        assertViewExist(id);
        return viewAction(id, typeText(content), ViewActions.closeSoftKeyboard());
    }

    /**
     * Input the text of the target textbox control
     *
     * @param viewMatcher The viewMatcher of the target control
     * @param content     The text of the target control
     * @return View Interaction
     */
    public static ViewInteraction inputText(Matcher<View> viewMatcher, String content) {
        assertViewExist(viewMatcher);
        return viewAction(viewMatcher, typeText(content), ViewActions.closeSoftKeyboard());
    }

    /**
     * Replace the text of the target textbox control
     *
     * @param id      The ID of the target control
     * @param content The text of the target control
     * @return View Interaction
     */
    public static ViewInteraction setText(@IdRes int id, String content) {
        waitForTime(1000);
        assertViewExist(id);
        return viewAction(id, setTextInTextView(content), ViewActions.closeSoftKeyboard());
    }

    /**
     * Replace the text of the target textbox control
     *
     * @param viewMatcher The viewMatcher of the target control
     * @param content     The text of the target control
     * @return View Interaction
     */
    public static ViewInteraction setText(Matcher<View> viewMatcher, String content) {
        waitForTime(1000);
        assertViewExist(viewMatcher);
        return viewAction(viewMatcher, setTextInTextView(content), ViewActions.closeSoftKeyboard());
    }

    public static ViewInteraction checkText(@IdRes int id, String content) {
        return findViewInteraction(id).check(matches(withText(content)));
    }

    public static ViewInteraction checkText(Matcher<View> viewMatcher, String content) {
        return onView(viewMatcher).check(matches(withText(content)));
    }

    public static ViewInteraction containsText(@IdRes int id, String content) {
        return findViewInteraction(id).check(matches(withSubstring(content)));
    }

    public static ViewInteraction containsText(Matcher<View> viewMatcher, String content) {
        return onView(viewMatcher).check(matches(withSubstring(content)));
    }

    public static ViewInteraction checkLocalDrawable(@IdRes int id, @DrawableRes int drawableId) {
        return findViewInteraction(id).check(matches(isAimLocalDrawable(drawableId)));
    }

    public static ViewInteraction checkLocalDrawable(Matcher<View> viewMatcher, @DrawableRes int drawableId) {
        return onView(viewMatcher).check(matches(isAimLocalDrawable(drawableId)));
    }

    public static ViewInteraction viewClick(@IdRes int id) {
        assertViewExist(id);
        return viewAction(id, click());
    }

    public static ViewInteraction viewClick(String text) {
        assertViewExist(text);
        return viewAction(text, click());
    }

    public static ViewInteraction viewClick(@IdRes int id, String text) {
        assertViewExist(id, text);
        return viewAction(id, text, click());
    }

    public static ViewInteraction viewClick(Matcher<View> viewMatcher) {
        assertViewExist(viewMatcher);
        return viewAction(viewMatcher, click());
    }

    public static Matcher<View> findViewMatcherInGroup(@IdRes int parentId, String childTypeClassName, int index) {
        return allOf(withParent(withId(parentId)), withClassName(is(childTypeClassName)), withParentIndex(index));
    }

    public static ViewInteraction findViewInteraction(@IdRes int id) {
        return onView(withId(id));
    }

    public static ViewInteraction findViewInteractionInGroup(@IdRes int parentId, String childTypeClassName, int index) {
        return onView(findViewMatcherInGroup(parentId, childTypeClassName, index));
    }

    public static <T extends RecyclerView.ViewHolder> ViewInteraction actionOnRecyclerItemWithHolder(@IdRes int recyclerViewId, Matcher<T> holderMatcher, final ViewAction action) {
        return viewAction(recyclerViewId, actionOnHolderItem(holderMatcher, action));
    }

    public static <T extends RecyclerView.ViewHolder> ViewInteraction clickOnRecyclerItemWithHolder(@IdRes int recyclerViewId, Matcher<T> holderMatcher) {
        return viewAction(recyclerViewId, actionOnHolderItem(holderMatcher, click()));
    }

    public static ViewInteraction actionOnRecyclerItemWithPosition(@IdRes int recyclerViewId, int position, final ViewAction action) {
        return viewAction(recyclerViewId, actionOnItemAtPosition(position, action));
    }

    public static ViewInteraction clickOnRecyclerItemWithPosition(@IdRes int recyclerViewId, int position) {
        return viewAction(recyclerViewId, actionOnItemAtPosition(position, click()));
    }

    public static <T extends RecyclerView.ViewHolder> PositionableRecyclerViewAction actionOnHolderItem(Matcher<T> holderMatcher, ViewAction action) {
        return RecyclerViewActions.actionOnHolderItem(holderMatcher, action);
    }

    public static ViewAction actionOnItemAtPosition(int position, ViewAction action) {
        return RecyclerViewActions.actionOnItemAtPosition(position, action);
    }

    /**
     * Wait
     *
     * @param millis The waiting time in milliseconds
     */
    public static void waitForTime(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static BoundedMatcher<View, ImageView> isAimLocalDrawable(@DrawableRes int id) {
        return new BoundedMatcher<View, ImageView>(ImageView.class) {
            @Override
            protected boolean matchesSafely(ImageView item) {
                if (item == null) {
                    return false;
                }
                Context context = item.getContext();
                if (context == null) {
                    return false;
                }
                return item.getDrawable().getCurrent().getConstantState()
                        == context.getResources().getDrawable(id).getConstantState();
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("is same drawable");
            }
        };
    }

    /**
     * Waiting for the target control display
     *
     * @param id       The ID of the target control
     * @param timeout  The timeout duration
     * @param interval The interval duration in milliseconds. Try to search for matches
     * @return The result whether the target control appears
     */
    public static boolean waitForView(@IdRes int id, long timeout, long interval) {
        long startMiles = System.currentTimeMillis();

        while (!isViewDisplayed(onView(withId(id)))) {
            if (System.currentTimeMillis() - startMiles > timeout) {
                return false;
            }
            waitForTime(interval);
        }

        return true;
    }

    /**
     * Waiting for the target control display
     *
     * @param text     The text of the target control
     * @param timeout  The timeout duration
     * @param interval The interval duration in milliseconds. Try to search for matches
     * @return The result whether the target control appears
     */
    public static boolean waitForView(String text, long timeout, long interval) {
        long startMiles = System.currentTimeMillis();
        while (!isViewDisplayed(onView(withText(text)))) {
            if (System.currentTimeMillis() - startMiles > timeout) {
                return false;
            }
            waitForTime(interval);
        }
        return true;
    }

    /**
     * Waiting for the target control display
     *
     * @param id       The ID of the target control
     * @param text     The text of the target control
     * @param timeout  The timeout duration
     * @param interval The interval duration in milliseconds. Try to search for matches
     * @return The result whether the target control appears
     */
    public static boolean waitForView(@IdRes int id, String text, long timeout, long interval) {
        long startMiles = System.currentTimeMillis();
        while (!isViewDisplayed(onView(allOf(withId(id), withText(text))))) {
            if (System.currentTimeMillis() - startMiles > timeout) {
                return false;
            }
            waitForTime(interval);
        }
        return true;
    }

    /**
     * Waiting for the target control display
     *
     * @param viewMatcher The viewMatcher of the target control
     * @param timeout     The timeout duration
     * @param interval    The interval duration in milliseconds. Try to search for matches
     * @return The result whether the target control appears
     */
    public static boolean waitForView(Matcher<View> viewMatcher, long timeout, long interval) {
        long startMiles = System.currentTimeMillis();
        while (!isViewDisplayed(onView(viewMatcher))) {
            if (System.currentTimeMillis() - startMiles > timeout) {
                return false;
            }
            waitForTime(interval);
        }
        return true;
    }

    /**
     * Waiting for the target control display
     *
     * @param parentId The ID of the parent control of the target control
     * @param id       The ID of the target control
     * @param timeout  The timeout duration
     * @param interval The interval duration in milliseconds. Try to search for matches
     * @return The result whether the target control appears
     */
    public static boolean waitForViewInGroup(@IdRes int parentId, @IdRes int id, long timeout, long interval) {
        long startMiles = System.currentTimeMillis();

        while (!isViewDisplayed(findViewInGroup(parentId, id))) {
            if (System.currentTimeMillis() - startMiles > timeout) {
                return false;
            }
            waitForTime(interval);
        }

        return true;
    }

    /**
     * Waiting for the target control display
     *
     * @param parentId The ID of the parent control of the target control
     * @param text     The text of the target control
     * @param timeout  The timeout duration
     * @param interval The interval duration in milliseconds. Try to search for matches
     * @return The result whether the target control appears
     */
    public static boolean waitForViewInGroup(@IdRes int parentId, String text, long timeout, long interval) {
        long startMiles = System.currentTimeMillis();

        while (!isViewDisplayed(findViewInGroup(parentId, text))) {
            if (System.currentTimeMillis() - startMiles > timeout) {
                return false;
            }
            waitForTime(interval);
        }

        return true;
    }

    /**
     * View whether the target control appears
     *
     * @param viewInteraction The viewInteraction of the target control
     * @return
     */
    public static boolean isViewDisplayed(ViewInteraction viewInteraction) {
        try {
            viewInteraction.check(ViewAssertions.matches(isDisplayed()));
            return true;
        } catch (Throwable var2) {
            return false;
        }
    }

    /**
     * Check whether the target control will appear in specified time
     *
     * @param text The text of the target control
     */
    public static void assertViewExist(String text) {
        assertTrue(waitForView(text, 10000L, 1000L));
    }

    /**
     * Check whether the target control will appear in specified time
     *
     * @param id The ID of the target control
     */
    public static void assertViewExist(@IdRes int id) {
        assertTrue(waitForView(id, 10000L, 1000L));
    }

    /**
     * Check whether the target control will appear in specified time
     *
     * @param id The ID of the target control
     */
    public static void assertViewExist(@IdRes int id, String text) {
        assertTrue(waitForView(id, text, 10000L, 1000L));
    }

    /**
     * Check whether the target control will appear in specified time
     *
     * @param viewMatcher The viewMatcher of the target control
     */
    public static void assertViewExist(Matcher<View> viewMatcher) {
        assertTrue(waitForView(viewMatcher, 10000L, 1000L));
    }

    /**
     * Check whether the target control will appear in specified time
     *
     * @param parentId The ID of the parent control of the target control
     * @param text     The text of the target control
     */
    public static void assertViewExistInGroup(@IdRes int parentId, String text) {
        assertTrue(waitForViewInGroup(parentId, text, 10000L, 1000L));
    }

    /**
     * Check whether the target control will appear in specified time
     *
     * @param parentId The ID of the parent control of the target control
     * @param id       The ID of the target control
     */
    public static void assertViewExistInGroup(@IdRes int parentId, @IdRes int id) {
        assertTrue(waitForViewInGroup(parentId, id, 10000L, 1000L));
    }

    public static ViewInteraction clickViewInGroup(@IdRes int parentId, String text) {
        return findViewInGroup(parentId, text).perform(click());
    }

    public static ViewInteraction clickViewInGroup(@IdRes int parentId, @IdRes int id) {
        return findViewInGroup(parentId, id).perform(click());
    }

    public static ViewInteraction findViewInGroup(@IdRes int parentId, String text) {
        return onView(allOf(withParent(withId(parentId)), withText(text)));
    }

    public static ViewInteraction findViewInGroup(@IdRes int parentId, @IdRes int id) {
        return onView(allOf(withParent(withId(parentId)), withId(id)));
    }

    public static void pressBack() {
        Espresso.pressBackUnconditionally();
    }

    public static ViewAction setTextInTextView(final String value) {
        return new ViewAction() {
            @SuppressWarnings("unchecked")
            @Override
            public Matcher<View> getConstraints() {
                return allOf(isDisplayed(), isAssignableFrom(TextView.class));
            }

            @Override
            public void perform(UiController uiController, View view) {
                ((TextView) view).setText(value);
            }

            @Override
            public String getDescription() {
                return "replace text";
            }
        };
    }

    /**
     * Click controls（compatible with system dialog boxes）
     * @param text The text of the target control
     */
    public static void clickUI(String text) {
        waitForTime(1000);
        UiObject uiObject = UITestRegisterHelper.getDevice().findObject(new UiSelector()
                .text(text));

        // Simulate a user-click on the OK button, if found.
        try {
            if (uiObject.exists() && uiObject.isEnabled()) {
                uiObject.click();
            }
        } catch (UiObjectNotFoundException e) {
            Log.e(TAG, "clickUI error", e);
        }
    }

    /**
     * Click controls（compatible with system dialog boxes）
     * @param resourceId The resource ID of the target control
     */
    public static void clickUIWithResourceId(String resourceId) {
        UiObject uiObject = UITestRegisterHelper.getDevice().findObject(new UiSelector()
                .resourceId(resourceId));

        // Simulate a user-click on the OK button, if found.
        try {
            if (uiObject.exists() && uiObject.isEnabled()) {
                uiObject.click();
            }
        } catch (UiObjectNotFoundException e) {
            Log.e(TAG, "clickUI error", e);
        }
    }
}
