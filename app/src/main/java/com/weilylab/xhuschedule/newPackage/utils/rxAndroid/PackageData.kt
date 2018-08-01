package com.weilylab.xhuschedule.newPackage.utils.rxAndroid

import com.weilylab.xhuschedule.newPackage.config.Status

class PackageData<T>(val status: Status,
					 val data: T?,
					 val error: Throwable?) {

	companion object {
		fun <T> content(data: T?): PackageData<T> = PackageData(Status.Content, data, null)
		fun <T> error(data: T?, error: Throwable?): PackageData<T> = PackageData(Status.Error, data, error)
		fun <T> error(error: Throwable?): PackageData<T> = error(null, error)
		fun <T> empty(data: T?): PackageData<T> = PackageData(Status.Empty, data, null)
		fun <T> empty(): PackageData<T> = empty(null)
		fun <T> loading(data: T?): PackageData<T> = PackageData(Status.Loading, data, null)
		fun <T> loading(): PackageData<T> = loading(null)
	}
}