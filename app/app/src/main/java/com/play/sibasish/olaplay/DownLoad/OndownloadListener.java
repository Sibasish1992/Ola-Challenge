package com.play.sibasish.olaplay.DownLoad;

import java.io.Serializable;

/**
 * Created by Sibasish on 16/12/17.
 */
public interface OndownloadListener extends Serializable {
    public void OnComplete(int id,String s_location,String i_location);
    public void OnFailure(int id);
    public void OnDestroyed(int id);
}
