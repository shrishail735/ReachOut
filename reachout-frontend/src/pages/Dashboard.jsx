import { useState, useEffect } from 'react';
import {
  BarChart, Bar, XAxis, YAxis, CartesianGrid,
  Tooltip, PieChart, Pie, Cell, ResponsiveContainer, Legend
} from 'recharts';
import { getApplications } from '../api/applications';
import api from '../api/axios';
import toast from 'react-hot-toast';

const STATUS_COLORS = {
  APPLIED: '#4f46e5',
  PHONE_SCREEN: '#f59e0b',
  INTERVIEW: '#3b82f6',
  OFFER: '#10b981',
  REJECTED: '#ef4444'
};

export default function Dashboard() {
  const [stats, setStats] = useState(null);
  const [applications, setApplications] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      const [statsRes, appsRes] = await Promise.all([
        api.get('/dashboard/stats'),
        getApplications()
      ]);
      setStats(statsRes.data);
      setApplications(appsRes.data);
    } catch {
      toast.error('Failed to load dashboard');
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <div style={styles.loading}>Loading analytics...</div>;

  // data for bar chart
  const barData = [
    { name: 'Applied', count: stats.applied, color: STATUS_COLORS.APPLIED },
    { name: 'Phone Screen', count: stats.phoneScreen, color: STATUS_COLORS.PHONE_SCREEN },
    { name: 'Interview', count: stats.interview, color: STATUS_COLORS.INTERVIEW },
    { name: 'Offer', count: stats.offer, color: STATUS_COLORS.OFFER },
    { name: 'Rejected', count: stats.rejected, color: STATUS_COLORS.REJECTED },
  ];

  // data for pie chart
  const pieData = barData.filter(d => d.count > 0);

  // weekly activity — group applications by week
  const weeklyData = getWeeklyData(applications);

  return (
    <div style={styles.page}>
      {/* Stat Cards */}
      <div style={styles.cardGrid}>
        <StatCard label="Total Applications" value={stats.totalApplications} color="#4f46e5" />
        <StatCard label="Interviews" value={stats.interview} color="#3b82f6" />
        <StatCard label="Offers" value={stats.offer} color="#10b981" />
        <StatCard label="Offer Rate" value={`${stats.offerRate.toFixed(1)}%`} color="#f59e0b" />
      </div>

      {/* Charts Row */}
      <div style={styles.chartsRow}>
        {/* Bar Chart */}
        <div style={styles.chartCard}>
          <h3 style={styles.chartTitle}>Applications by Status</h3>
          <ResponsiveContainer width="100%" height={220}>
            <BarChart data={barData} margin={{ top: 10, right: 10, left: -20, bottom: 0 }}>
              <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
              <XAxis dataKey="name" tick={{ fontSize: 11 }} />
              <YAxis tick={{ fontSize: 11 }} />
              <Tooltip />
              <Bar dataKey="count" radius={[4, 4, 0, 0]}>
                {barData.map((entry, index) => (
                  <Cell key={index} fill={entry.color} />
                ))}
              </Bar>
            </BarChart>
          </ResponsiveContainer>
        </div>

        {/* Pie Chart */}
        <div style={styles.chartCard}>
          <h3 style={styles.chartTitle}>Status Distribution</h3>
          {pieData.length > 0 ? (
            <ResponsiveContainer width="100%" height={220}>
              <PieChart>
                <Pie
                  data={pieData}
                  dataKey="count"
                  nameKey="name"
                  cx="50%"
                  cy="50%"
                  outerRadius={80}
                  label={({ name, percent }) =>
                    `${name} ${(percent * 100).toFixed(0)}%`
                  }
                  labelLine={false}
                >
                  {pieData.map((entry, index) => (
                    <Cell key={index} fill={entry.color} />
                  ))}
                </Pie>
                <Tooltip />
              </PieChart>
            </ResponsiveContainer>
          ) : (
            <div style={styles.noData}>No data yet</div>
          )}
        </div>
      </div>

      {/* Weekly Activity Chart */}
      <div style={{ ...styles.chartCard, marginTop: '16px' }}>
        <h3 style={styles.chartTitle}>Weekly Application Activity</h3>
        <ResponsiveContainer width="100%" height={200}>
          <BarChart data={weeklyData} margin={{ top: 10, right: 10, left: -20, bottom: 0 }}>
            <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
            <XAxis dataKey="week" tick={{ fontSize: 11 }} />
            <YAxis tick={{ fontSize: 11 }} />
            <Tooltip />
            <Bar dataKey="count" fill="#4f46e5" radius={[4, 4, 0, 0]} name="Applications" />
          </BarChart>
        </ResponsiveContainer>
      </div>
    </div>
  );
}

// helper — groups applications by week
function getWeeklyData(applications) {
  const weeks = {};
  applications.forEach(app => {
    const date = new Date(app.appliedDate);
    const weekStart = new Date(date);
    weekStart.setDate(date.getDate() - date.getDay());
    const key = weekStart.toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
    weeks[key] = (weeks[key] || 0) + 1;
  });

  return Object.entries(weeks)
    .map(([week, count]) => ({ week, count }))
    .slice(-6); // last 6 weeks
}

function StatCard({ label, value, color }) {
  return (
    <div style={styles.statCard}>
      <div style={{ ...styles.statValue, color }}>{value}</div>
      <div style={styles.statLabel}>{label}</div>
    </div>
  );
}

const styles = {
  page: { padding: '1.5rem', background: '#f0f4f8', minHeight: '100%' },
  loading: { display: 'flex', alignItems: 'center', justifyContent: 'center', height: '300px', color: '#666' },
  cardGrid: { display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: '16px', marginBottom: '16px' },
  statCard: { background: 'white', borderRadius: '12px', padding: '1.25rem', boxShadow: '0 1px 4px rgba(0,0,0,0.08)' },
  statValue: { fontSize: '2rem', fontWeight: 800, marginBottom: '4px' },
  statLabel: { fontSize: '13px', color: '#888', fontWeight: 500 },
  chartsRow: { display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' },
  chartCard: { background: 'white', borderRadius: '12px', padding: '1.25rem', boxShadow: '0 1px 4px rgba(0,0,0,0.08)' },
  chartTitle: { margin: '0 0 1rem', fontSize: '14px', fontWeight: 600, color: '#1a1a2e' },
  noData: { display: 'flex', alignItems: 'center', justifyContent: 'center', height: '200px', color: '#ccc', fontSize: '14px' }
};