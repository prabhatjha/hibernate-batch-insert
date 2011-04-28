package org.eejot.batchinsert;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;

public class DBService {
	SessionFactory sessionFactory = null;
	int hibernateBatchSize;

	public Session getHibernateSession()
	{
		try {
			if (sessionFactory == null) {
				AnnotationConfiguration configuration = new AnnotationConfiguration().configure();
				hibernateBatchSize = new Integer(configuration.getProperty("hibernate.jdbc.batch_size")).intValue();
				sessionFactory = configuration.buildSessionFactory();
			}
			return sessionFactory.getCurrentSession();
		}
		catch (Exception e)  {
			System.out.println(e.toString());
		}
		return null;
	}

	public void saveMembers(List<Member> members) throws HibernateException {
		Session session = null;
		Transaction transaction = null;
		
		
		Iterator<Member> it = members.iterator();

		while (it.hasNext())
		{
			try {
				session = getHibernateSession();
				transaction = session.beginTransaction();

				int numRecordsProcessed = 0;

				while (numRecordsProcessed < hibernateBatchSize && it.hasNext())
				{
					Member wsBean = it.next();
					session.save(wsBean);
					//log.info("Webservices record " + wsBean + " saved.");
					numRecordsProcessed++;
				}
				session.flush();
			} catch (HibernateException e) {
				throw new HibernateException("Cannot save member", e);
			} finally {
				transaction.commit();
			}
		}

	}

	public static void main (String[] args)  {
		DBService service = new DBService();
		List<Member> mems = new ArrayList<Member> ();

		Member m = null;
		for (int i = 0; i < 25; i++) {
			m = new Member();		
			m.setName("name" + i);
			mems.add(m);
		}
		service.saveMembers(mems);

	}



}
