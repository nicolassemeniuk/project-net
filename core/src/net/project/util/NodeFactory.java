/* 
 * Copyright 2000-2009 Project.net Inc.
 *
 * This file is part of Project.net.
 * Project.net is free software: you can redistribute it and/or modify it under the terms of 
 * the GNU General Public License as published by the Free Software Foundation, version 3 of the License.
 * 
 * Project.net is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Project.net.
 * If not, see http://www.gnu.org/licenses/gpl-3.0.html
*/
package net.project.util;

import java.util.LinkedList;
import java.util.List;

public class NodeFactory {
    private final List<Node> nodes;
    private final List<String> columns;

    public Node nextNode() {
        final Node node = new Node(this.columns);
        this.nodes.add(node);
        node.setId(this.nodes.size());
        return node;
    }

    public NodeFactory(List<String> columns) {
        this.nodes = new LinkedList<Node>();
        this.columns = columns;
    }

    public List<Node> getNodes() {
        return this.nodes;
    }
}