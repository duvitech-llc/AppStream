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

import android.support.v7.media.MediaRouteProvider;
import android.support.v7.media.MediaRouteProviderService;

public class DuvitechMediaRouteProviderService extends MediaRouteProviderService {

    @Override
    public MediaRouteProvider onCreateMediaRouteProvider() {
        return new DuvitechMediaRouteProvider(this);
    }
}
