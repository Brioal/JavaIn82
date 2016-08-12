package com.brioal.javain82.interfaces;

import java.util.List;

/**
 * Created by Brioal on 2016/7/11.
 */

public interface onListDoneListener {
    void success(List list);

    void failed(String message);
}
