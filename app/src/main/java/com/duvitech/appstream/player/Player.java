package com.duvitech.appstream.player;
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

import android.content.Context;
import android.support.v7.media.MediaControlIntent;
import android.support.v7.media.MediaRouter;

public abstract class Player {
    protected Callback mCallback;

    public abstract boolean isRemotePlayback();
    public abstract boolean isQueuingSupported();

    public abstract void connect(MediaRouter.RouteInfo route);
    public abstract void release();

    // basic operations that are always supported
    public abstract void play(final PlaylistItem item);
    public abstract void seek(final PlaylistItem item);
    public abstract void getStatus(final PlaylistItem item, final boolean update);
    public abstract void pause();
    public abstract void resume();
    public abstract void stop();

    // advanced queuing (enqueue & remove) are only supported
    // if isQueuingSupported() returns true
    public abstract void enqueue(final PlaylistItem item);
    public abstract PlaylistItem remove(String iid);

    // route statistics
    public void updateStatistics() {}
    public String getStatistics() { return ""; }

    // presentation display
    public void updatePresentation() {}

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public static Player create(Context context, MediaRouter.RouteInfo route) {
        Player player;
        if (route != null && route.supportsControlCategory(
                MediaControlIntent.CATEGORY_REMOTE_PLAYBACK)) {
            player = new RemotePlayer(context);
        } else {
            return null;
        }
        player.connect(route);
        return player;
    }

    public interface Callback {
        void onError();
        void onCompletion();
        void onPlaylistChanged();
        void onPlaylistReady();
    }
}
