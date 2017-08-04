package com.duvitech.appstream.provider;
/*
  ******************************************************************************
  * @file    ${FILE_NAME}
  * @author  George Vigelette
  * @version v1.0.0
  * @date    8/3/2017
  * @brief   
  ******************************************************************************
  * @attention
  *
  * <h2><center>&copy; COPYRIGHT 2017 Duvitech</center></h2>
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  ******************************************************************************
*/

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.media.MediaControlIntent;
import android.support.v7.media.MediaRouteDescriptor;
import android.support.v7.media.MediaRouteProvider;
import android.support.v7.media.MediaRouteProviderDescriptor;
import android.support.v7.media.MediaRouter.ControlRequestCallback;
import android.support.v7.media.MediaSessionStatus;
import android.support.v7.media.MediaRouter;
import android.util.Log;

import com.duvitech.appstream.R;
import com.duvitech.appstream.player.Player;
import com.duvitech.appstream.player.SessionManager;

import java.util.ArrayList;

public final class DuvitechMediaRouteProvider extends MediaRouteProvider{
    private static final String TAG = "DuvitechRouteProvider";

    private static final String VARIABLE_VOLUME_BASIC_ROUTE_ID = "variable_basic";
    private static final String FIXED_VOLUME_ROUTE_ID = "fixed";
    private static final int VOLUME_MAX = 10;

    public static final String CATEGORY_DUVITECH_ROUTE =
            "com.duvitech.appstream.CATEGORY_DUVITECH_ROUTE";
    public static final String DATA_PLAYBACK_COUNT =
            "com.duvitech.appstream.EXTRA_PLAYBACK_COUNT";
    public static final String ACTION_GET_STATISTICS =
            "com.duvitech.appstream.ACTION_GET_STATISTICS";

    private int mVolume = 5;
    private int mEnqueueCount;

    private static final ArrayList<IntentFilter> CONTROL_FILTERS_BASIC;
    static {
        IntentFilter videoPlayback = new IntentFilter();
        videoPlayback.addCategory(MediaControlIntent.CATEGORY_REMOTE_PLAYBACK);
        videoPlayback.addAction(MediaControlIntent.ACTION_PLAY);
        videoPlayback.addDataScheme("http");
        videoPlayback.addDataScheme("https");
        videoPlayback.addDataScheme("rtsp");
        addDataTypeUnchecked(videoPlayback, "video/*");

        IntentFilter playControls = new IntentFilter();
        playControls.addCategory(MediaControlIntent.CATEGORY_REMOTE_PLAYBACK);
        playControls.addAction(MediaControlIntent.ACTION_SEEK);
        playControls.addAction(MediaControlIntent.ACTION_GET_STATUS);
        playControls.addAction(MediaControlIntent.ACTION_PAUSE);
        playControls.addAction(MediaControlIntent.ACTION_RESUME);
        playControls.addAction(MediaControlIntent.ACTION_STOP);

        CONTROL_FILTERS_BASIC = new ArrayList<IntentFilter>();
        CONTROL_FILTERS_BASIC.add(videoPlayback);
        CONTROL_FILTERS_BASIC.add(playControls);
    }

    private static void addDataTypeUnchecked(IntentFilter filter, String type) {
        try {
            filter.addDataType(type);
        } catch (IntentFilter.MalformedMimeTypeException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void publishRoutes() {
        Resources r = getContext().getResources();
        // Create a route descriptor using previously created IntentFilters
        MediaRouteDescriptor routeDescriptor = new MediaRouteDescriptor.Builder(
                VARIABLE_VOLUME_BASIC_ROUTE_ID,
                r.getString(R.string.variable_volume_basic_route_name))
                .setDescription(r.getString(R.string.sample_route_description))
                .addControlFilters(CONTROL_FILTERS_BASIC)
                .setPlaybackStream(AudioManager.STREAM_MUSIC)
                .setPlaybackType(MediaRouter.RouteInfo.PLAYBACK_TYPE_REMOTE)
                .setVolumeHandling(MediaRouter.RouteInfo.PLAYBACK_VOLUME_VARIABLE)
                .setVolumeMax(VOLUME_MAX)
                .setVolume(mVolume)
                .build();
        // Add the route descriptor to the provider descriptor
        MediaRouteProviderDescriptor providerDescriptor =
                new MediaRouteProviderDescriptor.Builder()
                        .addRoute(routeDescriptor)
                        .build();

        // Publish the descriptor to the framework
        setDescriptor(providerDescriptor);
    }

    public DuvitechMediaRouteProvider(Context context) {
        super(context);
        publishRoutes();
    }

    private final class DuvitechRouteController extends MediaRouteProvider.RouteController {
        private final String mRouteId;
        private final SessionManager mSessionManager = new SessionManager("mrp");
        private final Player mPlayer;
        private PendingIntent mSessionReceiver;

        public DuvitechRouteController(String routeId) {
            mRouteId = routeId;
            mPlayer = Player.create(getContext(), null);
            if(mPlayer == null){
                /* failed to create player */
            }

            Log.d(TAG, mRouteId + ": Controller created");
        }

        @Override
        public void onRelease() {
            Log.d(TAG, mRouteId + ": Controller released");
        }

        @Override
        public void onSelect() {
            Log.d(TAG, mRouteId + ": Selected");
        }

        @Override
        public void onUnselect() {
            Log.d(TAG, mRouteId + ": Unselected");
        }

        @Override
        public void onSetVolume(int volume) {
            Log.d(TAG, mRouteId + ": Set volume to " + volume);
            if (!mRouteId.equals(FIXED_VOLUME_ROUTE_ID)) {
            }
        }

        @Override
        public void onUpdateVolume(int delta) {
            Log.d(TAG, mRouteId + ": Update volume by " + delta);
            if (!mRouteId.equals(FIXED_VOLUME_ROUTE_ID)) {
            }
        }

        @Override
        public boolean onControlRequest(Intent intent, MediaRouter.ControlRequestCallback callback) {
            Log.d(TAG, mRouteId + ": Received control request " + intent);
            String action = intent.getAction();
            if (intent.hasCategory(MediaControlIntent.CATEGORY_REMOTE_PLAYBACK)) {
                boolean success = false;
                if (action.equals(MediaControlIntent.ACTION_PLAY)) {
                    success = handlePlay(intent, callback);
                } else if (action.equals(MediaControlIntent.ACTION_ENQUEUE)) {
                    success = handleEnqueue(intent, callback);
                } else if (action.equals(MediaControlIntent.ACTION_REMOVE)) {
                    success = handleRemove(intent, callback);
                } else if (action.equals(MediaControlIntent.ACTION_SEEK)) {
                    success = handleSeek(intent, callback);
                } else if (action.equals(MediaControlIntent.ACTION_GET_STATUS)) {
                    success = handleGetStatus(intent, callback);
                } else if (action.equals(MediaControlIntent.ACTION_PAUSE)) {
                    success = handlePause(intent, callback);
                } else if (action.equals(MediaControlIntent.ACTION_RESUME)) {
                    success = handleResume(intent, callback);
                } else if (action.equals(MediaControlIntent.ACTION_STOP)) {
                    success = handleStop(intent, callback);
                } else if (action.equals(MediaControlIntent.ACTION_START_SESSION)) {
                    success = handleStartSession(intent, callback);
                } else if (action.equals(MediaControlIntent.ACTION_GET_SESSION_STATUS)) {
                    success = handleGetSessionStatus(intent, callback);
                } else if (action.equals(MediaControlIntent.ACTION_END_SESSION)) {
                    success = handleEndSession(intent, callback);
                }
                Log.d(TAG, mSessionManager.toString());
                return success;
            }

            if (action.equals(ACTION_GET_STATISTICS)
                    && intent.hasCategory(CATEGORY_DUVITECH_ROUTE)) {
                Bundle data = new Bundle();
                data.putInt(DATA_PLAYBACK_COUNT, mEnqueueCount);
                if (callback != null) {
                    callback.onResult(data);
                }
                return true;
            }

            return false;
        }

        private boolean handleEndSession(Intent intent, ControlRequestCallback callback) {
            return false;
        }

        private boolean handleGetSessionStatus(Intent intent, ControlRequestCallback callback) {
            return false;
        }

        private boolean handleStartSession(Intent intent, ControlRequestCallback callback) {
            return false;
        }

        private boolean handleStop(Intent intent, ControlRequestCallback callback) {
            return false;
        }

        private boolean handleResume(Intent intent, ControlRequestCallback callback) {
            return false;
        }

        private boolean handlePause(Intent intent, ControlRequestCallback callback) {
            return false;
        }

        private boolean handleGetStatus(Intent intent, ControlRequestCallback callback) {
            return false;
        }

        private boolean handleSeek(Intent intent, ControlRequestCallback callback) {
            return false;
        }

        private boolean handleRemove(Intent intent, ControlRequestCallback callback) {
            return false;
        }

        private boolean handleEnqueue(Intent intent, ControlRequestCallback callback) {
            return false;
        }

        private boolean handlePlay(Intent intent, ControlRequestCallback callback) {
            return false;
        }
    }
}
