/*
 *                     GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 */

package com.weilylab.xhuschedule.model.response

class LoginResponse : CloudResponse() {
    lateinit var data: LoginData
}

class LoginData {
    lateinit var cookie: String
    lateinit var fbToken: String
}