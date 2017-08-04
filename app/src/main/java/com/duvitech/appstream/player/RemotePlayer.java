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
import android.support.v7.media.MediaRouter;

public class RemotePlayer extends Player {
    private static final String TAG = "RemotePlayer";

    private Context mContext;

    public RemotePlayer(Context context) {
        mContext = context;
    }

    @Override
    public boolean isRemotePlayback() {
        return false;
    }

    @Override
    public boolean isQueuingSupported() {
        return false;
    }

    @Override
    public void connect(MediaRouter.RouteInfo route) {

    }

    @Override
    public void release() {

    }

    @Override
    public void play(PlaylistItem item) {

    }

    @Override
    public void seek(PlaylistItem item) {

    }

    @Override
    public void getStatus(PlaylistItem item, boolean update) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void enqueue(PlaylistItem item) {

    }

    @Override
    public PlaylistItem remove(String iid) {
        return null;
    }
}
