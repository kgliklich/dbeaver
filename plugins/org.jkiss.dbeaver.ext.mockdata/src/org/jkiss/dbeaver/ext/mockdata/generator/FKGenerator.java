/*
 * DBeaver - Universal Database Manager
 * Copyright (C) 2010-2017 Serge Rider (serge@jkiss.org)
 * Copyright (C) 2010-2017 Eugene Fradkin (eugene.fradkin@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jkiss.dbeaver.ext.mockdata.generator;

import org.jkiss.dbeaver.DBException;
import org.jkiss.dbeaver.model.DBUtils;
import org.jkiss.dbeaver.model.data.DBDLabelValuePair;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;
import org.jkiss.dbeaver.model.struct.*;
import org.jkiss.dbeaver.model.struct.rdb.DBSTableForeignKeyColumn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class FKGenerator extends AbstractMockValueGenerator {

    private int numberRefRecords = 100;
    private List<Object> refValues = null;

    @Override
    public void init(DBSDataManipulator container, DBSAttributeBase attribute, Map<Object, Object> properties) throws DBException {
        super.init(container, attribute, properties);

        nullsPersent = 0;

        Integer numberRefRecords = (Integer) properties.get("numberRefRecords"); //$NON-NLS-1$
        if (numberRefRecords != null) {
            this.numberRefRecords = numberRefRecords;
        }
    }

    @Override
    public Object generateOneValue(DBRProgressMonitor monitor) throws DBException {
        if (refValues == null) {
            refValues = new ArrayList<>();
            List<DBSEntityReferrer> attributeReferrers = DBUtils.getAttributeReferrers(monitor, (DBSEntityAttribute) attribute);
            DBSEntityReferrer fk = attributeReferrers.get(0); // TODO only the first
            List<? extends DBSEntityAttributeRef> references = ((DBSEntityReferrer) fk).getAttributeReferences(monitor);

            DBSTableForeignKeyColumn column = (DBSTableForeignKeyColumn) references.iterator().next(); // TODO only the first !!!
            Collection<DBDLabelValuePair> values = readColumnValues(monitor, fk.getDataSource(), (DBSAttributeEnumerable) column.getReferencedColumn(), numberRefRecords);
            for (DBDLabelValuePair value : values) {
                refValues.add(value.getValue());
            }
        }
        return refValues.get(random.nextInt(refValues.size()));
    }
}
