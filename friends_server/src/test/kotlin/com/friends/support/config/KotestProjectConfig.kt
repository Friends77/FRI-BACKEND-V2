package com.friends.support.config

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.spec.IsolationMode
import io.kotest.extensions.spring.SpringTestExtension
import io.kotest.extensions.spring.SpringTestLifecycleMode

class KotestProjectConfig : AbstractProjectConfig() {
    override val parallelism = (Runtime.getRuntime().availableProcessors() * 1.5).toInt()

    override fun extensions() = listOf(SpringTestExtension(SpringTestLifecycleMode.Test)) // Transactional을 사용하여 롤백을 하기 위해선 이 부분이 필요하다.

    override val isolationMode: IsolationMode = IsolationMode.InstancePerLeaf
}
