/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2022, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.wildfly.clustering.ejb.cache.bean;

import java.util.Map;
import java.util.function.Consumer;

import org.wildfly.clustering.ee.Mutator;
import org.wildfly.clustering.ejb.bean.BeanInstance;

/**
 * A default {@link BeanGroup} implementation based on a map of bean instances.
 * @author Paul Ferraro
 * @param <K> the bean identifier type
 * @param <V> the bean instance type
 */
public class DefaultBeanGroup<K, V extends BeanInstance<K>> extends DefaultImmutableBeanGroup<K, V> implements MutableBeanGroup<K, V> {

    private final Map<K, V> instances;
    private final Consumer<Map<K, V>> prePassivateTask;
    private final Mutator mutator;

    public DefaultBeanGroup(K id, Map<K, V> instances, Consumer<Map<K, V>> prePassivateTask, Mutator mutator, Runnable closeTask) {
        super(id, instances, closeTask);
        this.instances = instances;
        this.prePassivateTask = prePassivateTask;
        this.mutator = mutator;
    }

    @Override
    public void addBeanInstance(V instance) {
        this.instances.put(instance.getId(), instance);
    }

    @Override
    public V removeBeanInstance(K id) {
        return this.instances.remove(id);
    }

    @Override
    public void mutate() {
        this.prePassivateTask.accept(this.instances);
        this.mutator.mutate();
    }
}