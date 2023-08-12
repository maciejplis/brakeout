package com.matthias.breakout.ecs.system

import com.badlogic.ashley.core.EntitySystem
import com.matthias.breakout.ecs.component.*
import com.matthias.breakout.event.GameEvent
import com.matthias.breakout.event.GameEvent.BallBlockHit
import com.matthias.breakout.event.GameEventManager
import ktx.ashley.allOf
import ktx.ashley.exclude
import ktx.ashley.plusAssign
import ktx.log.logger

private val LOG = logger<BlockDestroySystem>()

private val ballFamily = allOf(BallComponent::class).exclude(RemoveComponent::class, StickyComponent::class).get()
private val blockFamily = allOf(BlockComponent::class).exclude(RemoveComponent::class).get()

class BlockDestroySystem(private val eventManager: GameEventManager<GameEvent>) : EntitySystem() {

    override fun update(delta: Float) {
        eventManager.getEventsOfType<BallBlockHit>()
            .filter { ballFamily.matches(it.ballEntity) }
            .filter { blockFamily.matches(it.blockEntity) }
            .forEach { event ->
                val blockC = event.blockEntity[BlockComponent::class]!!

                if (--blockC.lives == 0) {
                    LOG.debug { "Block destroyed" }

                    event.blockEntity += RemoveComponent().apply {
                        delay = 0.05f
                    }
                }
            }
    }
}