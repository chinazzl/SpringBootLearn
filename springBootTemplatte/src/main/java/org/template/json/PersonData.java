package org.template.json;

import com.google.common.collect.Lists;
import org.template.vo.Person;

import java.util.List;

/**********************************
 * @author zhang zhao lin
 * @date 2024年09月05日 15:19
 * @Description:
 **********************************/
public interface PersonData {

    List<Person> persons = Lists.newArrayList(
            new Person("一班",90,"一组",89,"孙悟空",89,"数学",98),
        new Person("一班",90,"一组",89,"唐僧",78,"语文",98),
        new Person("一班",90,"一组",89,"唐僧",78,"数学",78),
        new Person("一班",90,"二组",90,"沙悟净",90,"语文",90),
        new Person("一班",90,"二组",90,"沙悟净",90,"数学",90),
        new Person("二班",91,"一组",97,"鲁智深",98,"语文",89),
        new Person("二班",91,"一组",97,"鲁智深",98,"数学",98),
        new Person("二班",91,"二组",89,"宋江",89,"语文",98),
        new Person("二班",91,"二组",89,"宋江",89,"数学",78),
        new Person("二班",91,"二组",89,"林冲",88,"语文",90),
        new Person("二班",91,"二组",89,"林冲",88,"数学",90)
    );
}
