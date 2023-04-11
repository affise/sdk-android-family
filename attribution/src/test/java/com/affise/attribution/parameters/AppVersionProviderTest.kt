package com.affise.attribution.parameters

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import com.affise.attribution.logs.LogsManager
import com.google.common.truth.Truth
import io.mockk.*
import org.junit.Test

/**
 * Test for [AppVersionProvider]
 */
class AppVersionProviderTest {

    @Test
    fun `verify provide returns version code from package manager`() {
        val packageName = "com.myApp"
        val versionNameMock = "2"

        val logsManager: LogsManager = mockk()

        val packageInfo: PackageInfo = PackageInfo().apply {
            versionName = versionNameMock
        }
        val packageManagerMock: PackageManager = mockk {
            every {
                getPackageInfo(packageName, 0)
            } returns packageInfo
        }
        val context: Context = mockk {
            every {
                packageManager
            } returns packageManagerMock

            every {
                getPackageName()
            } returns packageName
        }

        val provider = AppVersionProvider(context, logsManager)
        val actual = provider.provide()
        Truth.assertThat(actual).isEqualTo(versionNameMock)
        verifyAll {
            context.packageManager
            context.packageName
            packageManagerMock.getPackageInfo(packageName, 0)
        }
    }

    @Test
    fun `verify provide returns null when package manager throws exception`() {
        val packageName = "com.myApp"
        val exception = PackageManager.NameNotFoundException()

        val logsManager: LogsManager = mockk {
            every {
                addDeviceError(exception)
            } just Runs
        }

        val packageManagerMock: PackageManager = mockk {
            every {
                getPackageInfo(packageName, 0)
            } throws exception
        }
        val context: Context = mockk {
            every {
                packageManager
            } returns packageManagerMock

            every {
                getPackageName()
            } returns packageName
        }

        val provider = AppVersionProvider(context, logsManager)
        val actual = provider.provide()
        Truth.assertThat(actual).isNull()
        verifyAll {
            context.packageManager
            context.packageName
            packageManagerMock.getPackageInfo(packageName, 0)
            logsManager.addDeviceError(exception)
        }
    }

    @Test
    fun `verify provide returns null when package manager returns null`() {
        val packageName = "com.myApp"

        val logsManager: LogsManager = mockk()

        val packageManagerMock: PackageManager = mockk {
            every {
                getPackageInfo(packageName, 0)
            } returns null
        }
        val context: Context = mockk {
            every {
                packageManager
            } returns packageManagerMock

            every {
                getPackageName()
            } returns packageName
        }

        val provider = AppVersionProvider(context, logsManager)
        val actual = provider.provide()
        Truth.assertThat(actual).isNull()
        verifyAll {
            context.packageManager
            context.packageName
            packageManagerMock.getPackageInfo(packageName, 0)
        }
    }
}